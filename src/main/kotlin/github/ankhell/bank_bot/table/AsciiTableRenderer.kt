package github.ankhell.bank_bot.table

abstract class AsciiTableRenderer : TableRenderer {
    override fun render(
        headers: List<String>,
        rows: List<List<String>>,
        monospaced: Boolean,
        footer: List<String>?
    ): String {
        if (headers.isEmpty()) return ""

        val allRows = listOf(headers) + rows + listOfNotNull(footer)
        val colWidths = headers.indices.map { col ->
            allRows.maxOf { it.getOrNull(col)?.length ?: 0 }
        }

        fun formatRow(row: List<String>) =
            row.mapIndexed { i, cell -> cell.padEnd(colWidths[i]) }
                .joinToString(" | ")

        val separator = colWidths.joinToString("-|-") { "-".repeat(it) }

        return buildString {
            if (monospaced) appendLine("```")
            appendLine(formatRow(headers))
            appendLine(separator)
            rows.forEach { appendLine(formatRow(it)) }
            if (footer != null) {
                appendLine(separator)
                appendLine(formatRow(footer))
            }
            if (monospaced) append("```")
        }
    }
}