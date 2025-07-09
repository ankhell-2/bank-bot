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

class RemoveBankTest {

    private val auth = mockk<AuthorizationService>()
    private val banks = mockk<BanksService>()
    private val command = RemoveBank(auth,  banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(4uL)

    @Test
    fun `should call removeBank for given abbreviation`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("abbreviation" to "XYZ"))

        coEvery { banks.removeBank("XYZ", guildId) } returns "Removed"

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)
        assertEquals("Removed", result)
    }
}
