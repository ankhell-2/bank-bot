package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.RolePermission
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface RolePermissionRepository : JpaRepository<RolePermission, BigInteger> {
    fun findAllByGuildAndRoleIDIn(guild: Guild, roleIDs: Collection<BigInteger>): List<RolePermission>
}