package github.ankhell.bank_bot.commands.permissions

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.mockInteractionWithRole
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.RolePermission
import github.ankhell.bank_bot.jpa.repositories.RolePermissionRepository
import github.ankhell.bank_bot.service.AuthorizationService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals

class ListPermissionsCommandTest {

    private val auth = mockk<AuthorizationService>()
    private val repo = mockk<RolePermissionRepository>()
    private val command = ListPermissionsCommand(auth, repo)

    private val user = mockk<User>()
    private val guildId = Snowflake(2uL)
    private val roleId = 987654321uL

    @Test
    fun `should list permissions for role`() = runTest {
        val interaction = mockInteractionWithRole(guildId, user, roleId)

        val permissions = setOf(Permission.ADMIN, Permission.BANK_VIEW)
        val rp = RolePermission(roleId.toBigInteger(), Guild(guildId.value.toBigInteger()), permissions)

        coEvery { repo.findByIdOrNull(roleId.toBigInteger()) } returns rp

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)

        assertEquals("Permissions for role <@&$roleId> are: Administrator, View list of all banks", result)
    }

    @Test
    fun `should handle missing permission mapping`() = runTest {
        val interaction = mockInteractionWithRole(guildId, user, roleId)

        coEvery { repo.findByIdOrNull(roleId.toBigInteger()) } returns null

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)

        assertEquals("No permissions found for  role <@&$roleId>", result)
    }
}
