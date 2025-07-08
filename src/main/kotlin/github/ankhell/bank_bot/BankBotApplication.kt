package github.ankhell.bank_bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean


@SpringBootApplication
@ConfigurationPropertiesScan("github.ankhell.bank_bot")
@EnableCaching
class BankBotApplication{
	@Bean
	fun cacheManager(): CacheManager {
		return ConcurrentMapCacheManager("guilds","users")
	}
}

fun main(args: Array<String>) {
	runApplication<BankBotApplication>(*args)
}
