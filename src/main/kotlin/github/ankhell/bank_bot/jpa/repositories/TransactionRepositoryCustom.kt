package github.ankhell.bank_bot.jpa.repositories

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.entities.Transaction

interface TransactionRepositoryCustom {
    fun findFiltered(limit: Long, member: Member?, bank: Bank?, guildId: Snowflake): List<Transaction>
}