package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.Bank
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BankRepository : JpaRepository<Bank, UUID> {
    fun findAllByGuildId(guildID: Snowflake): Set<Bank>

    fun findByGuildIdAndShortName(guildID: Snowflake, shortName: String): Bank?
}