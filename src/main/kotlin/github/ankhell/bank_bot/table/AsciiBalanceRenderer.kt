package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Balance
import org.springframework.stereotype.Component

@Component
class AsciiBalanceRenderer : AsciiTableRenderer(), BalanceTableRenderer {

    override fun render(balances: Set<Balance>): String {
        val headers = listOf("Short", "Full Name", "Balance")
        val rows = balances.map {
            listOf(it.bank.shortName, it.bank.fullName, it.amount.toString())
        }
        return render(headers, rows)
    }
}