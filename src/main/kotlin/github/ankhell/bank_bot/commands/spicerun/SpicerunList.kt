package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import org.springframework.stereotype.Component


@Component
class SpicerunList(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_list"

    override val description: String = "List most recent spice runs "

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        number("limit", "Limits the amount of spice runs displayed") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_REGISTER) {
            spiceRunService.listRuns(
                guildId = guildId,
                limit = interaction.command.numbers["limit"]?.toLong()
            ).description
        }
    }
}