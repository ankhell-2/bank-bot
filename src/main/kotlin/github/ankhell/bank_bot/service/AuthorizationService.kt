package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import github.ankhell.bank_bot.properties.AuthServiceProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val repo: RolePermissionRepository,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
    private val authServiceProperties: AuthServiceProperties
) {

    private val log = LoggerFactory.getLogger(AuthorizationService::class.java)

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
    ): String {
        val hasPermission = hasPermission(user, guildId, permission)
        val isAdmin = hasPermission(user, guildId, Permission.ADMIN)
        val isGuildAdmin = run {
            if (authServiceProperties.useDiscordAdminFallback) {
                val permissions = user.asMember(guildId).permissions
                if (permissions != null){
                    permissions.contains(dev.kord.common.entity.Permission.Administrator)
                } else {
                    log.warn("Unable to get permissions for user ${user.username} in guild $guildId")
                    false
                }
            } else false
        }
        return if (hasPermission || isAdmin || isGuildAdmin) {
            action()
        } else {
            "You are not authorized to do that"
        }
    }

}