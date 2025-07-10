package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.Balance
import github.ankhell.bank_bot.jpa.entities.SpiceRun
import org.springframework.stereotype.Component

@Component
class AsciiSpiceRunRenderer : AsciiTableRenderer(), SpiceRunTableRenderer {

    override fun render(spiceRuns: List<SpiceRun>): String {
        val headers = listOf("Id", "spice gathered", "guild share", "craft bonus","participants")
        val rows = spiceRuns.map { spiceRun ->
            listOf(
                spiceRun.id.toString(),
                spiceRun.spiceGathered.toString(),
                "${spiceRun.config.guildShare}%",
                if (spiceRun.config.hasCraftBonus) "x" else " ",
                spiceRun.participants.joinToString { it.name }
            )
        }
        return render(headers, rows, true)
    }
}