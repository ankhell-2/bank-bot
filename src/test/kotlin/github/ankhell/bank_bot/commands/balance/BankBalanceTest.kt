package github.ankhell.bank_bot.commands.balance

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.TransactionService
import github.ankhell.bank_bot.table.BalanceTableRenderer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BankBalanceTest {
    private val auth = mockk<AuthorizationService>()
    private val service = mockk<TransactionService>()
    private val renderer = mockk<BalanceTableRenderer>()
    private val command = BankBalance(auth, service, renderer)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)

    @Test
    fun `should return rendered table if balances found`() = runTest {
        val balances = setOf(mockk<Balance>())
        val interaction = mockInteraction(guildId, user)

        coEvery { auth.ifAllowed(eq(user), eq(guildId), any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            coEvery { service.getBalances(guildId, null) } returns balances
            every { renderer.render(balances) } returns "Rendered!"
            block()
        }

        val result = command.process(interaction)
        assertEquals("Rendered!", result)
    }

    @Test
    fun `should return fallback message if no balances found`() = runTest {
        val interaction = mockInteraction(guildId, user)

        coEvery { auth.ifAllowed(eq(user), eq(guildId), any(), any()) } coAnswers  {
            val block = args[3] as suspend () -> String
            coEvery { service.getBalances(guildId, null) } returns emptySet()
            block()
        }

        val result = command.process(interaction)
        assertEquals("No balance data found", result)
    }
}