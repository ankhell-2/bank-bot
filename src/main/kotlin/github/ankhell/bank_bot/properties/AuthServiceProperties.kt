package github.ankhell.bank_bot.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("bot")
data class AuthServiceProperties(val useDiscordAdminFallback: Boolean = true)