package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.jpa.entities.Bank
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceRepository : JpaRepository<Balance, Bank>{
    fun findByBank(bank: Bank): Balance?

    fun findAllByGuildId(guildId: Snowflake): Set<Balance>
}