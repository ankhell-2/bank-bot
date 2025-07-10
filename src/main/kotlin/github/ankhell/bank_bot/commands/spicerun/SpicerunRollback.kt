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
class SpicerunRollback(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_rollback"

    override val description: String = "Rollback spice run"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        number("id", "Id of a space run to rollback, if not specified - the last one would be rolled back") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_REGISTER) {
            spiceRunService.rollbackRun(
                guildId = guildId,
                id = interaction.command.numbers["id"]?.toLong()
            ).description
        }
    }
}