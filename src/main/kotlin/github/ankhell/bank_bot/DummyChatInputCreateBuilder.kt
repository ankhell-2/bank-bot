package github.ankhell.bank_bot

import dev.kord.common.Locale
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest

class DummyChatInputCreateBuilder(
    override var description: String="",
    override var descriptionLocalizations: MutableMap<Locale, String>?=null,
    override var defaultMemberPermissions: Permissions?=null,
    override var defaultPermission: Boolean?=null,
    override val type: ApplicationCommandType= ApplicationCommandType.ChatInput,
    override var nsfw: Boolean?=null,
    override var name: String="",
    override var nameLocalizations: MutableMap<Locale, String>?=null,
    override var options: MutableList<OptionsBuilder>?=null
) : ChatInputCreateBuilder {
    override fun toRequest(): ApplicationCommandCreateRequest {
        TODO("Not yet implemented")
    }
}