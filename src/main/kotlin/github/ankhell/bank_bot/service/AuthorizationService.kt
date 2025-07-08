package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val repo: RolePermissionRepository,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService
) {

    suspend fun hasPermission(
        user: User,
        guildId: Snowflake,
        permission: Permission
    ): Boolean {
        val roleIds = user.asMember(guildId).roleIds.map { it.value.toBigInteger() }
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        val rolePerms = repo.findAllByGuildAndRoleIDIn(guild, roleIds)

        return rolePerms.any { permission in it.permissions }
    }

    suspend fun ifAllowed(
        user: User,
        guildId: Snowflake,
        permission: Permission,
        action: suspend () -> String
    ): String =
        if (hasPermission(user, guildId, permission) || hasPermission(user, guildId, Permission.ADMIN)) {
            action()
        } else {
            "You are not authorized to do that"
        }

}