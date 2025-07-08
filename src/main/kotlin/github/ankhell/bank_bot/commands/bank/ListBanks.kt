package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import org.springframework.stereotype.Component

@Component
class ListBanks(
    private val bankRepository: BankRepository,
    private val authorizationService: AuthorizationService,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService
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
                bankRepository.findAllByGuild(guild).forEach {
                    append("(")
                    append(it.shortName)
                    append(")")
                    append(" - ")
                    append(it.fullName)
                    append(" | ")
                    append(it.uuid)
                    append("\n")
                }
            }
        }
    }

}