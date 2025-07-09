package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import org.springframework.stereotype.Component
import java.util.*

@Component
class ModifyBank(
    private val authorizationService: AuthorizationService,
    private val banksService: BanksService
) : Command {

    override val command: String = "bankmodify"
    override val description: String = "Modify bank"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("uuid", "Bank UUID") {
            required = true
        }
        string("abbreviation", "Short name of a bank") {
            required = false
        }
        string("fullname", "New full name of a bank") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_MANAGE) {
            val abbr = interaction.command.strings["abbreviation"]
            val fullName = interaction.command.strings["fullname"]
            val uuid = UUID.fromString(interaction.command.strings["uuid"]!!)
            banksService.modifyBank(uuid, abbr, fullName)
        }
    }


}