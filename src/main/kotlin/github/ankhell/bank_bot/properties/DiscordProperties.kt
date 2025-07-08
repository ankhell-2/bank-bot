package github.ankhell.bank_bot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("discord")
data class DiscordProperties(val token: String = "")