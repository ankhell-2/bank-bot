package github.ankhell.bank_bot.commands.transactions

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import github.ankhell.bank_bot.service.TransactionService
import github.ankhell.bank_bot.table.TransactionTableRenderer
import org.springframework.stereotype.Component

@Component
class Audit(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService,
    private val guildAndMemberRegistrarService: GuildAndMemberRegistrarService,
    private val banksService: BanksService,
    private val transactionTableRenderer: TransactionTableRenderer
) : Command {

    override val command: String = "audit"

    override val description: String = "Take a look at transactions log"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        number("limit", "Limits the amount of transactions shown, default is 10") {
            required = false
        }
        user("user", "Shows only transactions for that user") {
            required = false
        }
        string("bank", "Shows only transactions for selected bank") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        val guild = guildAndMemberRegistrarService.getGuild(guildId)
        val limit = interaction.command.numbers["limit"]?.toLong() ?: 10
        val member = interaction.command.users["user"]?.let { guildAndMemberRegistrarService.getUser(it) }
        val bank = interaction.command.strings["bank"]?.let { banksService.getBank(it,guild) }
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.TRANSACTION_VIEW) {
            transactionTableRenderer.render(transactionService.getTransactions(
                limit = limit,
                guild = guild,
                member = member,
                bank = bank
            ))
        }
    }
}