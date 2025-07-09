package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.SpiceRunConfig
import org.springframework.data.jpa.repository.JpaRepository

interface SpiceRunConfigRepository: JpaRepository<SpiceRunConfig, Long>{
    fun findTopByGuildIdOrderByIdDesc(guildId: Snowflake): SpiceRunConfig?
}