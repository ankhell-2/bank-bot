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
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals

class AssignPermissionsCommandTest {

    private val auth = mockk<AuthorizationService>()
    private val repo = mockk<RolePermissionRepository>()
    private val registrar = mockk<GuildAndMemberRegistrarService>()
    private val command = AssignPermissionsCommand(auth, repo, registrar)

    private val user = mockk<User>()
    private val guildId = Snowflake(1uL)
    private val roleId = 123456789uL

    @Test
    fun `should assign permission to new role`() = runTest {
        val interaction = mockInteractionWithRole(guildId, user, roleId, "BALANCE_VIEW")

        coEvery { repo.findByIdOrNull(roleId.toBigInteger()) } returns null
        coEvery { registrar.getGuild(guildId) } returns Guild(guildId.value.toBigInteger())
        coEvery { repo.save(any()) } returns mockk()

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)

        assertEquals("Permission to view bank balance successfully added to role <@&$roleId>", result)
    }

    @Test
    fun `should append permission to existing role`() = runTest {
        val existing = RolePermission(
            roleID = roleId.toBigInteger(),
            guild = Guild(guildId.value.toBigInteger()),
            permissions = setOf(Permission.BANK_MANAGE)
        )

        val interaction = mockInteractionWithRole(guildId, user, roleId, "BALANCE_VIEW")

        coEvery { repo.findByIdOrNull(roleId.toBigInteger()) } returns existing
        coEvery { repo.save(match { it.permissions.containsAll(existing.permissions + Permission.BALANCE_VIEW) }) } returns mockk()

        coEvery { auth.ifAllowed(user, guildId, any(), any()) } coAnswers {
            val block = args[3] as suspend () -> String
            block()
        }

        val result = command.process(interaction)

        assertEquals("Permission to view bank balance successfully added to role <@&$roleId>", result)
    }
}
