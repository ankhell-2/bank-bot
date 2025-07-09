package github.ankhell.bank_bot

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
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
    SpringApplicationBuilder(BankBotApplication::class.java)
        .web(WebApplicationType.NONE)
        .run(*args)

    // Keep the app alive manually
    Thread.currentThread().join()
}
