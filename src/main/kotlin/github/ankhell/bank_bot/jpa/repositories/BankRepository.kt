package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Guild
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BankRepository : JpaRepository<Bank, UUID> {
    fun findAllByGuild(guild: Guild): List<Bank>

    fun findByGuildAndShortName(guild: Guild, shortName: String): Bank?
}