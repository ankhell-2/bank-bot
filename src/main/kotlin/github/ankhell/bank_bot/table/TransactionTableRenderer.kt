package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Transaction

interface TransactionTableRenderer {
    fun render(transactions: List<Transaction>): String
}