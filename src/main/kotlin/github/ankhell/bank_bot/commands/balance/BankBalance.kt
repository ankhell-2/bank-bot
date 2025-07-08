package github.ankhell.bank_bot.commands.balance

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.TransactionService
import org.springframework.stereotype.Component

@Component
class BankBalance(
    private val authorizationService: AuthorizationService,
    private val transactionService: TransactionService
) : Command {

    override val command: String = "balance"

    override val description: String = "Shows bank(s) balance(s)"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("bankabbr", "Bank short name, if not specified - shows all") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BALANCE_MODIFY) {
            val bankShort: String? = interaction.command.strings["bankabbr"]
            transactionService.getBalances(guildId, bankShort)
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n") { "Bank ${it.bank.fullName} balance is ${it.amount}" }
                ?: "No balance data found"
        }
    }
}