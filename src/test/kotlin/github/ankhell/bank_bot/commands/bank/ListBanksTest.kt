package github.ankhell.bank_bot.commands.bank

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.BanksService
import github.ankhell.bank_bot.table.BanksTableRenderer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListBanksTest {

    private val auth = mockk<AuthorizationService>()
    private val renderer = mockk<BanksTableRenderer>()
    private val banks = mockk<BanksService>()
    private val command = ListBanks(auth,  renderer, banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(2uL)

    @Test
    fun `should render list of banks`() = runTest {
        coEvery { banks.listAllByGuild(guildId) } returns setOf()
        every { renderer.render(any()) } returns "Formatted Table"

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val interaction = mockInteraction(guildId, user)
        val result = command.process(interaction)

        assertEquals("Here is a list of available banks:\nFormatted Table", result)
    }
}
