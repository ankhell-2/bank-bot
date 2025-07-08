package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.UserCommandCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import kotlinx.coroutines.yield
import org.springframework.stereotype.Component

@Component
class AddBank(
    private val bankRepository: BankRepository,
    private val authorizationService: AuthorizationService,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService
) : Command {

    override val command: String = "bankadd"
    override val description: String = "Add a bank"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit= {
        string("abbreviation", "Abbreviation of a bank name") {
            required = true
        }
        string("fullname", "Full name of a bank") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_MANAGE) {
            val abbr = interaction.command.strings["abbreviation"]!!
            val fullName = interaction.command.strings["fullname"]!!
            if (bankRepository.findByGuildAndShortName(guild,abbr)!=null){
                return@ifAllowed "Bank with short name ($abbr) already exist in that guild!"
            }
            val bank = Bank(shortName = abbr, fullName = fullName, guild =  guild)
            bankRepository.save(bank)
            "Bank ($abbr) - $fullName successfully added"
        }
    }


}