package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import github.ankhell.bank_bot.table.BanksTableRenderer
import org.springframework.stereotype.Component

@Component
class ListBanks(
    private val authorizationService: AuthorizationService,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
    private val banksTableRenderer: BanksTableRenderer,
    private val banksService: BanksService
) : Command {

    override val command: String = "listbanks"

    override val description: String = "List all banks"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {}

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_VIEW) {
            buildString {
                append("Here is a list of available banks:\n")
                append(banksTableRenderer.render(banksService.listAllByGuild(guild)))
            }
        }
    }

}