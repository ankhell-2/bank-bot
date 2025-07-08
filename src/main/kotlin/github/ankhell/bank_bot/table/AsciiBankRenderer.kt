package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Bank
import org.springframework.stereotype.Component

@Component
class AsciiBankRenderer : AsciiTableRenderer(), BanksTableRenderer {

    override fun render(banks: Set<Bank>): String {
        val headers = listOf("Short", "Full Name", "UUID")
        val rows = banks.map {
            listOf(it.shortName, it.fullName, it.uuid.toString())
        }
        return render(headers, rows, true)
    }
}