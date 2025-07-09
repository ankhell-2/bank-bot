package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.RolePermission
import org.springframework.data.jpa.repository.JpaRepository

interface RolePermissionRepository : JpaRepository<RolePermission, Snowflake> {
    fun findAllByGuildIdAndIdIn(guildId: Snowflake, roleIDs: Collection<Snowflake>): List<RolePermission>
}