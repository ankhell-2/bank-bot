package github.ankhell.bank_bot.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.MemberRepository
import github.ankhell.bank_bot.service.MemberService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class GuildAndMemberRegistrarServiceTest {

    private lateinit var memberRepository: MemberRepository
    private lateinit var service: MemberService

    @BeforeEach
    fun setUp() {
        memberRepository = mockk()
        service = MemberService( memberRepository)
    }

    @Test
    fun `getUserOrNull should return member if found`() = runTest {
        val userId = Snowflake(1)
        val user: User = mockk {
            every { id } returns userId
        }
        val member = Member(userId, "username")

        coEvery { memberRepository.findByIdOrNull(member.id!!) } returns member

        val result = service.getUserOrNull(user)

        assertEquals(member, result)
    }

    // You can expand with `syncUser` test cases by mocking `user.asMember(guildId)` properly,
    // but that requires more setup for `KordMember`, which might be excessive for pure unit tests.
}
