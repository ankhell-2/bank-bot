package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger
import java.util.UUID

interface GuildRepository: JpaRepository<Guild, BigInteger>