package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.Result
import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.entities.Transaction
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.jpa.repositories.TransactionRepository
import github.ankhell.bank_bot.table.AsciiTransactionTableRenderer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.math.BigInteger

typealias BankAbbreviation = String

@Service
class TransactionService(
    private val balanceRepository: BalanceRepository,
    private val bankRepository: BankRepository,
    private val transactionRepository: TransactionRepository,
    private val memberService: MemberService,
    private val transactionTableRenderer: AsciiTransactionTableRenderer
) {

    @Transactional
    suspend fun performTransactionWithResult(
        user: User,
        sender: BankAbbreviation? = null,
        receiver: BankAbbreviation? = null,
        guildId: Snowflake,
        amount: Long,
        comment: String
    ): Result {
        if (sender == null && receiver == null) {
            return Result.failure("At least one of sender/receiver pair should be present")
        }

        val member = memberService.getUser(user)
        val senderBank = if (sender != null) {
            bankRepository.findByGuildIdAndShortName(guildId, sender)
                ?: return Result.failure("Bank ($sender) not found")
        } else null

        val receiverBank = if (receiver != null) {
            bankRepository.findByGuildIdAndShortName(guildId, receiver)
                ?: return Result.failure("Bank ($receiver) not found")
        } else null

        transactionRepository.save(
            Transaction(
                sender = senderBank,
                receiver = receiverBank,
                performedBy = member,
                amount = amount,
                comment = comment,
                guildId = guildId,
            )
        )

        if (senderBank != null) {
            val senderBalance = balanceRepository.findByBank(senderBank) ?: Balance(
                bank = senderBank,
                amount = BigInteger.ZERO,
                guildId = guildId
            )
            senderBalance.amount -= amount.toBigInteger()
            if (senderBalance.amount < BigInteger.ZERO) {
                return Result.failure("Sender bank ($sender) has insufficient balance to perform the transaction")
            }
            balanceRepository.save(senderBalance)
        }

        if (receiverBank != null) {
            val senderBalance = balanceRepository.findByBank(receiverBank) ?: Balance(
                bank = receiverBank,
                amount = BigInteger.ZERO,
                guildId = guildId
            )
            senderBalance.amount += amount.toBigInteger()
            balanceRepository.save(senderBalance)
        }

        return Result.success(when {
            senderBank == null && receiverBank != null -> "Successfully topped up bank ${receiverBank.fullName} for $amount"
            senderBank != null && receiverBank == null -> "Successfully withdrawn $amount from ${senderBank.fullName}"
            senderBank != null && receiverBank != null -> "Successfully transferred $amount from ${senderBank.fullName} to ${receiverBank.fullName}"
            else -> "Unknown state"
        })
    }

    @Transactional
    suspend fun performTransaction(
        user: User,
        sender: BankAbbreviation? = null,
        receiver: BankAbbreviation? = null,
        guildId: Snowflake,
        amount: Long,
        comment: String
    ): String {
        val (isSuccess, description) = performTransactionWithResult(user, sender, receiver, guildId, amount, comment)
        if (!isSuccess) TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        return description
    }

    @Transactional
    suspend fun getTransactionsRendered(
        limit: Long = 10,
        guildId: Snowflake,
        member: Member? = null,
        bank: Bank? = null,
    ): String = transactionTableRenderer.render(transactionRepository.findFiltered(limit, member, bank, guildId))

    @Transactional
    suspend fun getBalances(guildId: Snowflake, bankAbbreviation: String? = null): Set<Balance> {
        if (bankAbbreviation == null) {
            return balanceRepository.findAllByGuildId(guildId)
        } else {
            val bank = bankRepository.findByGuildIdAndShortName(guildId, bankAbbreviation)
            return bank?.let { balanceRepository.findByBank(it) }?.let { setOf(it) } ?: emptySet()
        }
    }

}