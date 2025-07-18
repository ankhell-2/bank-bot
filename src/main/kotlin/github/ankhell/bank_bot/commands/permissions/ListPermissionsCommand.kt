package github.ankhell.bank_bot.commands.permissions

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.role
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import github.ankhell.bank_bot.service.AuthorizationService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ListPermissionsCommand(
    private val authorizationService: AuthorizationService,
    private val permissionRepository: RolePermissionRepository,
) : Command {

    override val command: String = "list_permissions"

    override val description: String = "List permissions of a role"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        role("role", "Role to query permissions for") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String =
        authorizationService.ifAllowed(interaction.user, interaction.invokedCommandGuildId!!, Permission.ADMIN) {
            val roleID = interaction.command.roles["role"]!!.id
            val permEntity = permissionRepository.findByIdOrNull(roleID)
            if (permEntity!=null ){
                "Permissions for role <@&$roleID> are: ${permEntity.permissions.joinToString { it.description }}"
            } else {
                "No permissions found for  role <@&$roleID>"
            }
        }

}