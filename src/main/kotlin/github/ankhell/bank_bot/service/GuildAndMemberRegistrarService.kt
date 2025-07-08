package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.GuildRepository
import github.ankhell.bank_bot.jpa.repositories.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

typealias KordMember = dev.kord.core.entity.Member

@Service
class GuildAndMemberRegistrarService(
    private val guildRepository: GuildRepository,
    private val userRepository: MemberRepository
) {

    @Transactional
    fun syncGuild(guildId: Snowflake): Guild {
        val bigintId = guildId.value.toBigInteger()
        return guildRepository.findByIdOrNull(bigintId) ?: guildRepository.save(Guild(bigintId))
    }

    @Transactional
    suspend fun getGuildOrNull(guildId: Snowflake): Guild? = guildRepository.findByIdOrNull(guildId.value.toBigInteger())

    @Transactional
    suspend fun getGuild(guildId: Snowflake): Guild = getGuildOrNull(guildId)!!

    @Transactional
    suspend fun syncUser(user: User, guildId: Snowflake): Member {
        val memberEntity = getUserOrNull(user)
        val guild = getGuildOrNull(guildId)!!
        return if (memberEntity != null) {
            userRepository.save(
                memberEntity.copy(
                guilds = memberEntity.guilds.toMutableSet().apply { add(guild) },
                roleIds = memberEntity.roleIds.toMutableSet().apply { addAll( user.asMember(guildId).roleIds.map { it.value.toBigInteger() })}
            ))
        } else {
            userRepository.save(
                Member(
                    id = user.id.value.toBigInteger(),
                    username = user.username,
                    guilds = setOf(guild),
                    roleIds = user.asMember(guildId).roleIds.map { it.value.toBigInteger() }.toSet()
                )
            )
        }
    }

    @Transactional
    suspend fun getUserOrNull(user: User): Member? =
        userRepository.findByIdOrNull(user.id.value.toBigInteger())

    @Transactional
    suspend fun getUser(user: User): Member = getUserOrNull(user)!!

}