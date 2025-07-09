package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import org.springframework.stereotype.Component

@Component
class SpicerunConfigure(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_configure"

    override val description: String = "Configure spice runs parameters"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("back_bank", "Short name of a bank backing spice runs") {
            required = true
        }
        string("main_bank", "Short name of a bank to transfer guild share to") {
            required = true
        }
        number("guild_share", "Sets guild share for spice runs") {
            required = true
        }
        boolean("25bonus", "If we have 25% bonus to crafting costs this week") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.ADMIN) {
            spiceRunService.saveConfig(
                backingBank = interaction.command.strings["back_bank"]!!,
                mainBank = interaction.command.strings["main_bank"]!!,
                guildShare = interaction.command.numbers["guild_share"]!!,
                hasCraftBonus = interaction.command.booleans["25bonus"]!!,
                guildId = guildId,
            ).description
        }
    }
}