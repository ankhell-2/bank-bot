package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.number
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import github.ankhell.bank_bot.table.MinersTableRenderer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component


@Component
class SpicerunMinersList(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService,
    @Qualifier("uuid")
    private val minersTableRenderer: MinersTableRenderer
) : Command {

    override val command: String = "spicerun_miners_list"

    override val description: String = "List spice miners"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {}

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_LIST) {
            spiceRunService.getMiners(
                guildId = guildId,
                minersTableRenderer = minersTableRenderer
            ).description
        }
    }
}