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
class TopUpBalance(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService
) : Command {

    override val command: String = "topup"

    override val description: String = "Add money to the bank"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("bankabbr", "Bank short name") {
            required = true
        }
        number("ammount", "Ammount to add to bank balance") {
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BALANCE_MODIFY) {
            transactionService.topUpBank(
                user = interaction.user,
                bankAbbreviation = interaction.command.strings["bankabbr"]!!,
                guildId = guildId,
                amount = interaction.command.numbers["ammount"]!!.toLong()
            )
        }
    }
}