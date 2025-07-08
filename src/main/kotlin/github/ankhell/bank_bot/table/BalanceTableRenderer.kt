package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Balance

interface BalanceTableRenderer {
    fun render(balances: Set<Balance>): String
}