package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import org.springframework.stereotype.Component

@Component
class SpicerunDebt(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_debt"

    override val description: String = "Shows information about remaining payment debts to spice run participants"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = { }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_MINERS_DEBT_VIEW) {
            spiceRunService.getMiners(guildId).description
        }
    }
}