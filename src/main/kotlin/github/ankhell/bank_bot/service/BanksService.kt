package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BanksService(
    private val bankRepository: BankRepository,
    private val balanceRepository: BalanceRepository
) {

    @Transactional
    suspend fun addBank(shortName: String, fullName: String, guildID: Snowflake): String {
        if (bankRepository.findByGuildIdAndShortName(guildID, shortName) != null) {
            return "Bank with short name ($shortName) already exist in that guild!"
        }
        val bank = Bank(shortName = shortName, fullName = fullName, guildId = guildID)
        bankRepository.save(bank)
        return "Bank ($shortName) - $fullName successfully added"
    }

    @Transactional
    suspend fun listAllByGuild(guildID: Snowflake): Set<Bank> =
        bankRepository.findAllByGuildId(guildID).toSet()

    @Transactional
    suspend fun modifyBank(uuid: UUID, shortName: String?, fullName: String?): String {
        val bankEntity = bankRepository.findByIdOrNull(uuid)
        if (bankEntity == null) {
            return "Bank with uuid $uuid doesn't exist"
        }
        bankEntity.also { be ->
            shortName?.let { be.shortName = it }
            fullName?.let { be.fullName = it }
        }
        bankRepository.save(bankEntity)
        return "Bank ${bankEntity.fullName} modified successfully"
    }

    @Transactional
    suspend fun getBank(shortName: String, guildID: Snowflake): Bank? =
        bankRepository.findByGuildIdAndShortName(guildID, shortName)


    @Transactional
    suspend fun removeBank(shortName: String, guildID: Snowflake): String {
        val bank = bankRepository.findByGuildIdAndShortName(guildID, shortName)
        return if (bank != null) {
            val balance = balanceRepository.findByBank(bank)
            if (balance != null) {
                balance.isDeleted = true
                balanceRepository.save(balance)
            }
            bank.isDeleted = true
            bankRepository.save(bank)
            "Bank ($shortName) removed successfully"
        } else {
            "Bank ($shortName) doesn't exist"
        }
    }

}