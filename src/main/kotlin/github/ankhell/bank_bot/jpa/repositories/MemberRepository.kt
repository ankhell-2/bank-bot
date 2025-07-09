package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Snowflake>