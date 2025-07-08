package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface MemberRepository: JpaRepository<Member, BigInteger>