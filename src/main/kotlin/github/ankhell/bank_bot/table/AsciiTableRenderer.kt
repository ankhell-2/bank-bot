package github.ankhell.bank_bot.table

import org.springframework.stereotype.Component

@Component
class AsciiTableRenderer :TableRenderer {
    override fun render(headers: List<String>, rows: List<List<String>>): String {
        if (headers.isEmpty()) return ""

        val allRows = listOf(headers) + rows
        val colWidths = headers.indices.map { col ->
            allRows.maxOf { it[col].length }
        }

        fun formatRow(row: List<String>) =
            row.mapIndexed { i, cell -> cell.padEnd(colWidths[i]) }
                .joinToString(" | ")

        val separator = colWidths.joinToString("-|-") { "-".repeat(it) }

        return buildString {
            appendLine("```")
            appendLine(formatRow(headers))
            appendLine(separator)
            rows.forEach { appendLine(formatRow(it)) }
            append("```")
        }
    }
}