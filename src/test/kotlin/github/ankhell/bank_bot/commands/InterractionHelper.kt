package github.ankhell.bank_bot.commands

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import io.mockk.every
import io.mockk.mockk

fun mockInteraction(
    guildId: Snowflake,
    user: User,
    strings: Map<String, String> = emptyMap(),
    numbers: Map<String, Double> = emptyMap()
): ChatInputCommandInteraction {
    val command = mockk<InteractionCommand>(relaxed = true)
    every { command.strings } returns strings
    every { command.numbers } returns numbers

    return mockk {
        every { invokedCommandGuildId } returns guildId
        every { this@mockk.user } returns user
        every { this@mockk.command } returns command
    }
}

fun mockInteractionWithRole(
    guildId: Snowflake,
    user: User,
    roleId: Snowflake,
    permissionName: String? = null
): ChatInputCommandInteraction {
    val role = mockk<Role> {
        every { id } returns roleId
    }

    val command = mockk<InteractionCommand> {
        every { roles } returns mapOf("role" to role)
        every { strings } returns (permissionName?.let { mapOf("permission" to it) } ?: mapOf())
    }

    return mockk {
        every { this@mockk.user } returns user
        every { this@mockk.command } returns command
        every { invokedCommandGuildId } returns guildId
    }
}