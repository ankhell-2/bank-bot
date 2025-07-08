package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.jpa.entities.Transaction
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.jpa.repositories.TransactionRepository
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
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
) {

    @Transactional
    suspend fun performTransaction(
        user: User,
        sender: BankAbbreviation? = null,
        receiver: BankAbbreviation? = null,
        guildId: Snowflake,
        amount: Long,
        comment: String
    ): String {
        if (sender == null && receiver == null) {
            return "At least one of sender/receiver pair should be present"
        }


        val member = guildAndMemberRegistrarService.getUser(user)
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        val senderBank = if (sender != null) {
            bankRepository.findByGuildAndShortName(guild, sender)
                ?: return "Bank ($sender) not found"
        } else null

        val receiverBank = if (receiver != null) {
            bankRepository.findByGuildAndShortName(guild, receiver)
                ?: return "Bank ($receiver) not found"
        } else null

        transactionRepository.save(
            Transaction(
                sender = senderBank,
                receiver = receiverBank,
                performedBy = member,
                amount = amount,
                comment = comment,
                guild = guild,
            )
        )

        if (senderBank != null) {
            val senderBalance = balanceRepository.findByBank(senderBank) ?: Balance(
                bank = senderBank,
                amount = BigInteger.ZERO,
                guild = guild
            )
            val updatedSenderBalance = senderBalance.copy(
                amount = senderBalance.amount - amount.toBigInteger()
            )
            if (updatedSenderBalance.amount < BigInteger.ZERO) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return "Sender bank ($sender) has insufficient balance to perform the transaction"
            }
            balanceRepository.save(updatedSenderBalance)
        }

        if (receiverBank != null) {
            val senderBalance = balanceRepository.findByBank(receiverBank) ?: Balance(
                bank = receiverBank,
                amount = BigInteger.ZERO,
                guild = guild
            )
            val updatedSenderBalance = senderBalance.copy(
                amount = senderBalance.amount + amount.toBigInteger()
            )
            balanceRepository.save(updatedSenderBalance)
        }

        return when {
            senderBank == null && receiverBank != null -> "Successfully topped up bank ${receiverBank.fullName} for $amount"
            senderBank != null && receiverBank == null -> "Successfully withdrawn $amount from ${senderBank.fullName}"
            senderBank != null && receiverBank != null -> "Successfully transferred $amount from ${senderBank.fullName} to ${receiverBank.fullName}"
            else -> "Unknown state"
        }
    }

    @Transactional
    suspend fun getBalances(guildId: Snowflake, bankAbbreviation: String? = null): Set<Balance> {
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        if (bankAbbreviation == null) {
            return balanceRepository.findAllByGuild(guild)
        } else {
            val bank = bankRepository.findByGuildAndShortName(guild, bankAbbreviation)
            return bank?.let { balanceRepository.findByBank(it) }?.let { setOf(it) } ?: emptySet()
        }
    }

}