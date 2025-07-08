package github.ankhell.bank_bot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("discord")
data class DiscordProperties(val token: String = "")