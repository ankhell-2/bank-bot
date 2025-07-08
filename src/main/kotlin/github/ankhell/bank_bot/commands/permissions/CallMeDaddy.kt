package github.ankhell.bank_bot.commands.permissions

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.role
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.RolePermission
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import org.springframework.stereotype.Component

//TODO: Replace that shit with something more meaningfull
@Component
class CallMeDaddy(
    private val permissionRepository: RolePermissionRepository,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService
) : Command {

    override val command: String = "callmedaddy"
    override val description: String = "Cheat code to get admin permissions"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        role("role", "Role to add permissions to") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val rolePermission = RolePermission(
            roleID = interaction.command.roles["role"]!!.id.value.toBigInteger(),
            guild = guildAndMemberRegistrarService.getGuild(interaction.invokedCommandGuildId!!),
            permissions = setOf(Permission.ADMIN)
        )
        permissionRepository.save(rolePermission)
        return "You are now my daddy"
    }

}