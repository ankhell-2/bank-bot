package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.entities.Transaction

interface TransactionRepositoryCustom {
    fun findFiltered(limit: Long, member: Member?, bank: Bank?, guild: Guild): List<Transaction>
}