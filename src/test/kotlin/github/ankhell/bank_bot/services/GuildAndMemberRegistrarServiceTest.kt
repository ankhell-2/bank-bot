package github.ankhell.bank_bot.services

import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.GuildRepository
import github.ankhell.bank_bot.jpa.repositories.MemberRepository
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class GuildAndMemberRegistrarServiceTest {

    private lateinit var guildRepository: GuildRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var service: GuildAndMemberRegistrarService

    @BeforeEach
    fun setUp() {
        guildRepository = mockk()
        memberRepository = mockk()
        service = GuildAndMemberRegistrarService(guildRepository, memberRepository)
    }

    @Test
    fun `syncGuild should return existing guild if found`() {
        val guildId = Snowflake(12345)
        val guild = Guild(guildId.value.toBigInteger())

        every { guildRepository.findByIdOrNull(guild.id) } returns guild

        val result = service.syncGuild(guildId)

        assertEquals(guild, result)
        verify(exactly = 0) { guildRepository.save(any()) }
    }

    @Test
    fun `syncGuild should save and return new guild if not found`() {
        val guildId = Snowflake(12345)
        val guild = Guild(guildId.value.toBigInteger())

        every { guildRepository.findByIdOrNull(guild.id) } returns null
        every { guildRepository.save(any()) } returns guild

        val result = service.syncGuild(guildId)

        assertEquals(guild, result)
        verify { guildRepository.save(match { it.id == guild.id }) }
    }

    @Test
    fun `getGuildOrNull should return guild if found`() = runTest {
        val guildId = Snowflake(1)
        val guild = Guild(guildId.value.toBigInteger())

        coEvery { guildRepository.findByIdOrNull(guild.id) } returns guild

        val result = service.getGuildOrNull(guildId)

        assertEquals(guild, result)
    }

    @Test
    fun `getUserOrNull should return member if found`() = runTest {
        val userId = Snowflake(1)
        val user: User = mockk {
            every { id } returns userId
        }
        val member = Member(userId.value.toBigInteger(), "username", roleIds = emptySet())

        coEvery { memberRepository.findByIdOrNull(member.id!!) } returns member

        val result = service.getUserOrNull(user)

        assertEquals(member, result)
    }

    // You can expand with `syncUser` test cases by mocking `user.asMember(guildId)` properly,
    // but that requires more setup for `KordMember`, which might be excessive for pure unit tests.
}
