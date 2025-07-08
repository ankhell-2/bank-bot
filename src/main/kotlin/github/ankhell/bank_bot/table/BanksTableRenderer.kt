package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Bank

interface BanksTableRenderer {
    fun render(banks: Set<Bank>): String
}