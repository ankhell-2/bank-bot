package github.ankhell.bank_bot.table

import github.ankhell.bank_bot.jpa.entities.SpiceMiner
import org.springframework.stereotype.Component

private const val GUILD_MINER_ID = "guild"

@Component("debt")
class AsciiMinerRendererDebt : AsciiTableRenderer(), MinersTableRenderer {

    override fun render(miners: Set<SpiceMiner>): String {
        val headers = listOf("Name", "Debt")
        val rows = miners.map {
            listOf(it.name, it.debt.toString())
        }
        val footer = listOf("Total", miners.sumOf { it.debt }.toString())
        return render(headers, rows, true, footer)
    }
}

@Component("uuid")
class AsciiMinerRenderer : AsciiTableRenderer(), MinersTableRenderer {

    override fun render(miners: Set<SpiceMiner>): String {
        val headers = listOf("Name", "UUID")
        val rows = miners.filterNot { it.name == GUILD_MINER_ID }.map {
            listOf(it.name, it.uuid.toString())
        }
        return render(headers, rows, true)
    }
}