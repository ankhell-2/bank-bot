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
class SpicerunPay(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
): Command {

    override val command: String = "spicerun_pay"

    override val description: String = "Register information about payment done to spice run participant"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("name","Name of participant") {
            required = true
        }
        number("amount", "Amount of melange to pay") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_PAY) {
            spiceRunService.pay(
                minerName = interaction.command.strings["name"]!!,
                amount = interaction.command.numbers["amount"]!!.toLong(),
                guildId = guildId,
                user = interaction.user
            ).description
        }
    }
}