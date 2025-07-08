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
class PerformTransaction(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService
) : Command {

    override val command: String = "transaction"

    override val description: String = "Perform a transaction - if sender is null it's topup, if receiver - withdrawal"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        number("ammount", "Ammount to add to bank balance") {
            required = true
        }
        string("comment", "Comment about that transaction") {
            required = true
        }
        string("sender", "Sender bank short name") {
            required = false
        }
        string("receiver", "Receiver bank short name") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.TRANSACTION_CREATE) {
            transactionService.performTransaction(
                user = interaction.user,
                sender = interaction.command.strings["sender"],
                receiver = interaction.command.strings["receiver"],
                guildId = guildId,
                amount = interaction.command.numbers["ammount"]!!.toLong(),
                comment = interaction.command.strings["comment"]!!

            )
        }
    }
}