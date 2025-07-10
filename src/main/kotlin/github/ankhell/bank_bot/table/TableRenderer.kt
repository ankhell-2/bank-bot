package github.ankhell.bank_bot.table

interface TableRenderer {
    fun render(headers: List<String>, rows: List<List<String>>, monospaced: Boolean = true, footer: List<String>? = null): String
}