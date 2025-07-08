package github.ankhell.bank_bot.commands.bank

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.UserCommandCreateBuilder
import dev.kord.rest.builder.interaction.string
import github.ankhell.bank_bot.Permission
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.AuthorizationService
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import kotlinx.coroutines.yield
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ModifyBank(
    private val bankRepository: BankRepository,
    private val authorizationService: AuthorizationService,
) : Command {

    override val command: String = "bankmodify"
    override val description: String = "Modify bank"
    override val paramBuilder: ChatInputCreateBuilder.() -> Unit = {
        string("uuid", "Bank UUID") {
            required = true
        }
        string("abbreviation", "Short name of a bank") {
            required = false
        }
        string("fullname", "New full name of a bank") {
            required = false
        }
    }

    override suspend fun process(interaction: ChatInputCommandInteraction): String {
        val guildId = interaction.invokedCommandGuildId!!
        return authorizationService.ifAllowed(interaction.user, guildId, Permission.BANK_MANAGE) {
            val abbr = interaction.command.strings["abbreviation"]
            val fullName = interaction.command.strings["fullname"]
            val uuid = UUID.fromString(interaction.command.strings["uuid"]!!)
            val bankEntity = bankRepository.findByIdOrNull(uuid)
            if (bankEntity == null) {
                return@ifAllowed "Bank with uuid $uuid doesn't exist"
            }
            val newBank = bankEntity.copy(
                shortName = abbr ?: bankEntity.shortName,
                fullName = fullName ?: bankEntity.fullName
            )
            bankRepository.save(newBank)
            "Bank ${newBank.fullName} modified successfully"
        }
    }


}