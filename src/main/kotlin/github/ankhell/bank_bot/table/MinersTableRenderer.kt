package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.SpiceMiner

interface MinersTableRenderer {
    fun render(banks: Set<SpiceMiner>): String
}