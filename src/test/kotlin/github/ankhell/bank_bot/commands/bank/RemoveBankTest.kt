package github.ankhell.bank_bot.commands.bank

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.service.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RemoveBankTest {

    private val auth = mockk<AuthorizationService>()
    private val registrar = mockk<GuildAndMemberRegistrarService>()
    private val banks = mockk<BanksService>()
    private val command = RemoveBank(auth, registrar, banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(4uL)
    private val guild = Guild(4uL.toBigInteger())

    @Test
    fun `should call removeBank for given abbreviation`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf("abbreviation" to "XYZ"))

        coEvery { registrar.getGuild(guildId) } returns guild
        coEvery { banks.removeBank("XYZ", guild) } returns "Removed"

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)
        assertEquals("Removed", result)
    }
}
