package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.SpiceMiner
import org.springframework.stereotype.Component

@Component
class AsciiMinerRenderer : AsciiTableRenderer(), MinersTableRenderer {

    override fun render(banks: Set<SpiceMiner>): String {
        val headers = listOf("Name", "Debt")
        val rows = banks.map {
            listOf(it.name, it.debt.toString())
        }
        return render(headers, rows, true)
    }
}