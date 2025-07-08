package github.ankhell.bank_bot.commands

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder

interface Command {
    val command: String

    val description: String

    val paramBuilder: ChatInputCreateBuilder.() -> Unit

    suspend fun process(interaction: ChatInputCommandInteraction): String
}