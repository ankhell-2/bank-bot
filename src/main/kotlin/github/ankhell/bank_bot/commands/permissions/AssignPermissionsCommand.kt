package github.ankhell.bank_bot.commands.permissions

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.role
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.RolePermission
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import github.ankhell.bank_bot.service.AuthorizationService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class AssignPermissionsCommand(
    private val authorizationService: AuthorizationService,
    private val permissionRepository: RolePermissionRepository
) : Command {

    override val command: String = "assign_permission"

    override val description: String = "Assign permission to a role"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        role("role", "Role to add permissions to") {
            required = true
        }
        string("permission", "Permission to assign") {
            required = true
            Permission.entries.forEach { choice(it.description, it.name) }
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String =
        authorizationService.ifAllowed(interaction.user, interaction.invokedCommandGuildId!!, Permission.ADMIN) {
            val permission = Permission.valueOf(interaction.command.strings["permission"]!!)
            val roleID = interaction.command.roles["role"]!!.id
            val permEntity = permissionRepository.findByIdOrNull(roleID)
            val rolePermission = permEntity
                ?.apply { permissions.add(permission) }
                ?: RolePermission(
                    id = roleID,
                    guildId = interaction.invokedCommandGuildId!!,
                    permissions = mutableSetOf(permission)
                )
            permissionRepository.save(rolePermission)

            "Permission to ${permission.description.lowercase()} successfully added to role <@&$roleID>"
        }

}