package github.ankhell.bank_bot.commands.balance

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.TransactionService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TransferTest {
    private val auth = mockk<AuthorizationService>()
    private val service = mockk<TransactionService>()
    private val command = Transfer(auth, service)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)

    @Test
    fun `should reject negative amount`() = runTest {
        val interaction = mockInteraction(
            guildId,
            user,
            mapOf("sender" to "SRC", "receiver" to "DST", "comment" to "move"),
            mapOf("amount" to -5.0)
        )
        val result = command.process(interaction)
        assertEquals("Operation amount should be positive", result)
    }

    @Test
    fun `should perform transfer`() = runTest {
        val interaction = mockInteraction(
            guildId,
            user,
            mapOf("sender" to "SRC", "receiver" to "DST", "comment" to "ok"),
            mapOf("amount" to 42.0)
        )


        coEvery { auth.ifAllowed(eq(user), eq(guildId), any(), any()) } coAnswers  {
            val block = args[3] as suspend () -> String
            coEvery {
                service.performTransaction(
                    user = user,
                    sender = "SRC",
                    receiver = "DST",
                    guildId = guildId,
                    amount = 42L,
                    comment = "ok"
                )
            } returns "transferred"
            block()
        }

        val result = command.process(interaction)
        assertEquals("transferred", result)
    }
}
