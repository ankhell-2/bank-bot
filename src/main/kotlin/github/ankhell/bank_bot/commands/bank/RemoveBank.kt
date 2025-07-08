package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.UserCommandCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import kotlinx.coroutines.yield
import org.springframework.stereotype.Component

@Component
class RemoveBank(
    private val authorizationService: AuthorizationService,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
    private val banksService: BanksService
) : Command {

    override val command: String = "bankremove"
    override val description: String = "Remove a bank"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("abbreviation", "Abbreviation of a bank name") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_MANAGE) {
            val abbr = interaction.command.strings["abbreviation"]!!
            banksService.removeBank(abbr, guild)
        }
    }


}