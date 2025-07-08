package github.ankhell.bank_bot.commands.bank

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.service.*
import github.ankhell.bank_bot.table.BanksTableRenderer
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListBanksTest {

    private val auth = mockk<AuthorizationService>()
    private val registrar = mockk<GuildAndMemberRegistrarService>()
    private val renderer = mockk<BanksTableRenderer>()
    private val banks = mockk<BanksService>()
    private val command = ListBanks(auth, registrar, renderer, banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(2uL)
    private val guild = Guild(2uL.toBigInteger())

    @Test
    fun `should render list of banks`() = runTest {
        coEvery { registrar.getGuild(guildId) } returns guild
        coEvery { banks.listAllByGuild(guild) } returns setOf()
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
