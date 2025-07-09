package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import org.springframework.stereotype.Component

@Component
class SpicerunRegister(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_register"

    override val description: String = "Register information about spice run participants"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("names", "Coma separated array of names (case insensitive)") {
            required = true
        }
        number("amount", "Amount of spice gathered, default is 50000") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_PAY) {
            spiceRunService.registerRun(
                miners = interaction.command.strings["names"]!!.lowercase().split(",").toSet(),
                guildId = guildId,
                amount = interaction.command.numbers["amount"]?.toLong() ?: 50_000L
            ).description
        }
    }
}