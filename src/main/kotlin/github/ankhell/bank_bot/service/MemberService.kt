package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val userRepository: MemberRepository
) {

    @Transactional
    suspend fun syncUser(user: User, guildId: Snowflake): Member {
        val memberEntity = getUserOrNull(user) ?: Member(
            id = user.id,
            username = user.username,
            guildIds = mutableSetOf(guildId),
            roleIds = user.asMember(guildId).roleIds.toMutableSet()
        )

        if (memberEntity.id == user.id) {
            memberEntity.guildIds.add(guildId)
            memberEntity.roleIds.addAll(user.asMember(guildId).roleIds)
        }
        return userRepository.save(memberEntity)
    }

    @Transactional
    suspend fun getUserOrNull(user: User): Member? =
        userRepository.findByIdOrNull(user.id)

    @Transactional
    suspend fun getUser(user: User): Member = getUserOrNull(user)!!

}