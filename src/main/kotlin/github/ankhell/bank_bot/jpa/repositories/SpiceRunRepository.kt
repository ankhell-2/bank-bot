package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.SpiceRun
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SpiceRunRepository: JpaRepository<SpiceRun, Long>{

    fun findByIdAndGuildId(id: Long, guildId: Snowflake): SpiceRun

    fun findTopByGuildIdOrderByIdDesc(guildId: Snowflake): SpiceRun

    fun findByGuildIdOrderByIdDesc(guildId: Snowflake, pageable: Pageable): List<SpiceRun>

    @Query("SELECT r FROM SpiceRun r JOIN r.participants p WHERE p.id = :minerId")
    fun findAllByParticipant(@Param("minerId") minerId: UUID): List<SpiceRun>
}