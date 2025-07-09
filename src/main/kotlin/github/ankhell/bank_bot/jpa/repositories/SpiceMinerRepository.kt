package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.SpiceMiner
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpiceMinerRepository: JpaRepository<SpiceMiner, UUID>{

    fun findAllByGuildId(guildId: Snowflake): Set<SpiceMiner>

    fun findByGuildIdAndName(guildID: Snowflake, name: String): SpiceMiner?
}