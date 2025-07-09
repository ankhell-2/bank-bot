package github.ankhell.bank_bot.commands.bank

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AddBankTest {

    private val auth = mockk<AuthorizationService>()
    private val banks = mockk<BanksService>()
    private val command = AddBank(auth,  banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)

    @Test
    fun `should call addBank when authorized`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf(
            "abbreviation" to "ABC",
            "fullname" to "Alpha Beta Corp"
        ))

        coEvery { banks.addBank("ABC", "Alpha Beta Corp", guildId) } returns "Bank added!"

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)
        assertEquals("Bank added!", result)
    }
}