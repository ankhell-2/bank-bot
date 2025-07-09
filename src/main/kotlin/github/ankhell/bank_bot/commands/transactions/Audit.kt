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
import github.ankhell.bank_bot.service.MemberService
import github.ankhell.bank_bot.service.TransactionService
import org.springframework.stereotype.Component

@Component
class Audit(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService,
    private val memberService: MemberService,
    private val banksService: BanksService,
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
        val limit = interaction.command.numbers["limit"]?.toLong() ?: 10
        val member = interaction.command.users["user"]?.let { memberService.getUser(it) }
        val bank = interaction.command.strings["bank"]?.let { banksService.getBank(it,guildId) }
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.TRANSACTION_VIEW) {
            transactionService.getTransactionsRendered(
                limit = limit,
                guildId = guildId,
                member = member,
                bank = bank
            )
        }
    }
}