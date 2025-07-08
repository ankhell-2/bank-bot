package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Transaction
import org.springframework.stereotype.Component

@Component
class AsciiTransactionTableRenderer : AsciiTableRenderer(), TransactionTableRenderer{

    override fun render(transactions: List<Transaction>): String {
        val headers = listOf("Who", "Action", "Amount", "Comment", "Time")
        val rows = transactions.map { tx ->
            val who = "<@${tx.performedBy.id}>"
            val amount = tx.amount.toString()
            val comment = tx.comment
            val timestamp = tx.timestamp.toString()

            val action = when {
                tx.sender == null && tx.receiver != null ->
                    "Top up of ${tx.receiver!!.fullName}"

                tx.sender != null && tx.receiver == null ->
                    "Withdrawal from ${tx.sender!!.fullName}"

                tx.sender != null && tx.receiver != null ->
                    "Transfer from ${tx.sender!!.fullName} to ${tx.receiver!!.fullName}"

                else -> "Unknown"
            }

            listOf(who, action, amount, comment, timestamp)
        }

        return render(headers, rows, false)
    }
}