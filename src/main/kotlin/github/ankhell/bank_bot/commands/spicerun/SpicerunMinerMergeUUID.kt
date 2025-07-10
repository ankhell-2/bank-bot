package github.ankhell.bank_bot.commands.spicerun

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.SpiceRunService
import org.springframework.stereotype.Component
import java.util.UUID


@Component
class SpicerunMinerMergeUUID(
    private val authorizationService: AuthorizationService,
    private val spiceRunService: SpiceRunService
) : Command {

    override val command: String = "spicerun_miner_merge_uuid"

    override val description: String = "Merge miners"

    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("uuid","UUID of a main miner"){
            required = true
        }
        string("uuid_slave","UUID of a miner to merge into main"){
            required = true
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.SPICE_RUN_MINER_MERGE) {
            spiceRunService.mergeMiners(
                guildId = guildId,
                mainMinerId = UUID.fromString(interaction.command.strings["uuid"]!!),
                slaveMinerId = UUID.fromString(interaction.command.strings["uuid_slave"]!!)
            ).description
        }
    }
}