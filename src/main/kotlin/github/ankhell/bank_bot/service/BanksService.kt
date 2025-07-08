package github.ankhell.bank_bot.service

import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BanksService(
    private val bankRepository: BankRepository,
    private val balanceRepository: BalanceRepository
) {

    @Transactional
    suspend fun addBank(shortName: String, fullName: String, guild: Guild): String {
        if (bankRepository.findByGuildAndShortName(guild, shortName) != null) {
            return "Bank with short name ($shortName) already exist in that guild!"
        }
        val bank = Bank(shortName = shortName, fullName = fullName, guild = guild)
        bankRepository.save(bank)
        return "Bank ($shortName) - $fullName successfully added"
    }

    @Transactional
    suspend fun listAllByGuild(guild: Guild): Set<Bank> =
        bankRepository.findAllByGuild(guild).toSet()

    @Transactional
    suspend fun modifyBank(uuid: UUID, shortName: String?, fullName: String?): String {
        val bankEntity = bankRepository.findByIdOrNull(uuid)
        if (bankEntity == null) {
            return "Bank with uuid $uuid doesn't exist"
        }
        val newBank = bankEntity.copy(
            shortName = shortName ?: bankEntity.shortName,
            fullName = fullName ?: bankEntity.fullName
        )
        bankRepository.save(newBank)
        return "Bank ${newBank.fullName} modified successfully"
    }

    @Transactional
    suspend fun removeBank(shortName: String, guild: Guild): String {
        val bank = bankRepository.findByGuildAndShortName(guild, shortName)
        return if (bank != null) {
            val balance = balanceRepository.findByBank(bank)
            if (balance != null) {
                balanceRepository.save(balance.copy(isDeleted = true))
            }
            bankRepository.save(bank.copy(isDeleted = true))
            "Bank ($shortName) removed successfully"
        } else {
            "Bank ($shortName) doesn't exist"
        }
    }

}