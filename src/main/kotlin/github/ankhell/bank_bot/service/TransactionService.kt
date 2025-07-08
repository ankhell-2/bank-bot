package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.PartyType
import github.ankhell.bank_bot.jpa.entities.Transaction
import github.ankhell.bank_bot.jpa.entities.TransactionParty
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.jpa.repositories.TransactionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class TransactionService(
    private val balanceRepository: BalanceRepository,
    private val bankRepository: BankRepository,
    private val transactionRepository: TransactionRepository,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
) {

    @Transactional
    suspend fun topUpBank(user: User, bankAbbreviation: String, guildId: Snowflake, amount: Long): String {
        val member = guildAndMemberRegistrarService.getUser(user)
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        val bank = bankRepository.findByGuildAndShortName(guild, bankAbbreviation)
        if (bank == null) {
            return "Bank $bankAbbreviation doesn't exist"
        }

        transactionRepository.save(
            Transaction(
                sender = TransactionParty(
                    type = PartyType.USER,
                    userId = member.id!!
                ),
                receiver = TransactionParty(
                    type = PartyType.BANK,
                    bankId = bank.uuid
                ),
                amount = amount,
                guild = guild
            )
        )

        val balance =
            balanceRepository.findByBank(bank) ?: Balance(bank = bank, amount = BigInteger.ZERO, guild = guild)

        balanceRepository.save(balance.copy(amount = balance.amount.plus(amount.toBigInteger())))

        return "Successfully topped up ${bank.fullName} balance for $amount"
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