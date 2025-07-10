package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.SpiceRun

interface SpiceRunTableRenderer {
    fun render(spiceRuns: List<SpiceRun>): String
}