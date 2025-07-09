package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import org.springframework.stereotype.Component

@Component
class AddBank(
    private val authorizationService: AuthorizationService,
    private val banksService: BanksService
) : Command {

    override val command: String = "bank_add"
    override val description: String = "Add a bank"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("abbreviation", "Abbreviation of a bank name") {
            required = true
        }
        string("fullname", "Full name of a bank") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_MANAGE) {
            val abbr = interaction.command.strings["abbreviation"]!!
            val fullName = interaction.command.strings["fullname"]!!
            banksService.addBank(abbr, fullName, guildId)
        }
    }


}