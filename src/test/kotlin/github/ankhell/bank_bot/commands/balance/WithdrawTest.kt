package github.ankhell.bank_bot.commands.balance

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.TransactionService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WithdrawTest {
    private val auth = mockk<AuthorizationService>()
    private val service = mockk<TransactionService>()
    private val command = Withdraw(auth, service)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)

    @Test
    fun `should reject negative amount`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("bank" to "SRC", "comment" to "take"), mapOf("amount" to -1.0))
        val result = command.process(interaction)
        assertEquals("Operation amount should be positive", result)
    }

    @Test
    fun `should perform withdrawal`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("bank" to "SRC", "comment" to "reason"), mapOf("amount" to 20.0))

        coEvery { auth.ifAllowed(eq(user), eq(guildId), any(), any()) } coAnswers  {
            val block = args[3] as suspend () -> String
            coEvery {
                service.performTransaction(
                    user = user,
                    sender = "SRC",
                    guildId = guildId,
                    amount = 20L,
                    comment = "reason"
                )
            } returns "withdrawn"
            block()
        }

        val result = command.process(interaction)
        assertEquals("withdrawn", result)
    }
}
