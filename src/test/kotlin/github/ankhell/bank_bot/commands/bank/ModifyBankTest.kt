package github.ankhell.bank_bot.commands.bank

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.commands.mockInteraction
import github.ankhell.bank_bot.service.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class ModifyBankTest {

    private val auth = mockk<AuthorizationService>()
    private val banks = mockk<BanksService>()
    private val command = ModifyBank(auth, banks)

    private val user = mockk<User>()
    private val guildId = Snowflake(3uL)
    private val uuid = UUID.randomUUID()

    @Test
    fun `should call modifyBank with given args`() = runTest {
        val interaction = mockInteraction(guildId, user, mapOf(
            "uuid" to uuid.toString(),
            "abbreviation" to "NEW",
            "fullname" to "New Bank Name"
        ))

        coEvery {
            banks.modifyBank(uuid, "NEW", "New Bank Name")
        } returns "Modified successfully"

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)
        assertEquals("Modified successfully", result)
    }
}
