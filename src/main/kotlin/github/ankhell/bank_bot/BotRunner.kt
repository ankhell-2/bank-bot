package github.ankhell.bank_bot

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.properties.DiscordProperties
import github.ankhell.bank_bot.service.GuildAndMemberRegistrarService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class BotRunner(
    private val props: DiscordProperties,  // bind token from application.yaml
    commandsV2: Set<Command>,
    private val registrarService: GuildAndMemberRegistrarService
) : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Default + job
    lateinit var kord: Kord
    private val commandMap = commandsV2.associateBy { it.command }


    @PostConstruct
    fun startBot() {
        launch {
            kord = Kord(props.token)

            kord.guilds.collect { guild ->
                val guildId = guild.id
                kord.getGuildApplicationCommands(guildId).collect {
                    kord.rest.interaction.deleteGuildApplicationCommand(kord.selfId, guildId, it.id)
                }
                commandMap.forEach { name, cmd ->
                    launch {
                        kord.createGuildChatInputCommand(
                            guildId = guildId,
                            name = name,
                            description = cmd.description,
                            builder = cmd.paramBuilder
                        )
                    }
                }
                registrarService.syncGuild(guildId)
            }

            kord.on<ChatInputCommandInteractionCreateEvent> {
                val guildId = interaction.invokedCommandGuildId!!
                registrarService.syncGuild(guildId)
                registrarService.syncUser(interaction.user, guildId)
                interaction.respondEphemeral {
                    content = commandMap[interaction.command.rootName]!!.process(interaction)
                }
                println(interaction.command)
            }

            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    @PreDestroy
    fun shutdown() = runBlocking {
        job.cancel() // 🔁 request coroutine cancellation first

        if (this@BotRunner::kord.isInitialized) {
            kord.logout() // 🧼 gracefully disconnect from Discord
        }

        job.cancelAndJoin() // 🧹 wait for remaining jobs to finish
    }
}