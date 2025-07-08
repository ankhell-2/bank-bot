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

class TopUpTest {
    private val auth = mockk<AuthorizationService>()
    private val service = mockk<TransactionService>()
    private val command = TopUp(auth, service)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)

    @Test
    fun `should reject negative amount`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("bank" to "RCV", "comment" to "test"), mapOf("amount" to -100.0))
        val result = command.process(interaction)
        assertEquals("Operation amount should be positive", result)
    }

    @Test
    fun `should delegate to transactionService`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("bank" to "RCV", "comment" to "ok"), mapOf("amount" to 100.0))

        coEvery { auth.ifAllowed(eq(user), eq(guildId), any(), any()) } coAnswers  {
            val block = args[3] as suspend () -> String
            coEvery {
                service.performTransaction(user, receiver = "RCV", guildId = guildId, amount = 100L, comment = "ok")
            } returns "done"
            block()
        }

        val result = command.process(interaction)
        assertEquals("done", result)
    }
}
