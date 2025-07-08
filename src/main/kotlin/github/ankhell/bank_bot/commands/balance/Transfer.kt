package github.ankhell.bank_bot.commands.balance

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.TransactionService
import org.springframework.stereotype.Component

@Component
class Transfer(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService
) : Command {

    override val command: String = "transfer"

    override val description: String = "Transfer funds between banks"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("sender","Sender bank short name") {
            required = true
        }
        string("receiver","Receiver bank short name") {
            required = true
        }
        number("amount", "Amount to withdraw") {
            required = true
        }
        string("comment", "Comment about that transaction") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        val amount = interaction.command.numbers["amount"]!!.toLong()
        if (amount < 0) return "Operation amount should be positive"
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.TRANSACTION_CREATE) {
            transactionService.performTransaction(
                user = interaction.user,
                sender = interaction.command.strings["sender"],
                receiver = interaction.command.strings["receiver"],
                guildId = guildId,
                amount = amount,
                comment = interaction.command.strings["comment"]!!
            )
        }
    }
}