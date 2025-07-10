package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.SpiceRun
import org.springframework.data.jpa.repository.JpaRepository

interface SpiceRunRepository: JpaRepository<SpiceRun, Long>{

    fun findByIdAndGuildId(id: Long, guildId: Snowflake): SpiceRun

    fun findTopByGuildIdOrderByIdDesc(guildId: Snowflake): SpiceRun
}