package github.ankhell.bank_bot

import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import github.ankhell.bank_bot.commands.Command
import github.ankhell.bank_bot.properties.DiscordProperties
import github.ankhell.bank_bot.service.MemberService
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class BotRunner(
    private val props: DiscordProperties,
    commandsV2: Set<Command>,
    private val registrarService: MemberService
) : CoroutineScope, ApplicationRunner {
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Default + job
    private val commandMap = commandsV2.associateBy { it.command }
    private val log = LoggerFactory.getLogger(BotRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        launch {
            val kord = Kord(props.token)

            kord.guilds.collect { guild ->
                val guildId = guild.id

                val guildRegisteredCommands = kord.getGuildApplicationCommands(guildId).toList().associateBy { it.name }

                commandMap.forEach { name, command ->
                    val existingCommand = guildRegisteredCommands[name]
                    if (!commandEqual(existingCommand, command)) {
                        if (existingCommand != null) {
                            log.info("Found difference for command ${command.command}, reregistering")
                            launch {
                                kord.rest.interaction.deleteGuildApplicationCommand(
                                    kord.selfId,
                                    guildId,
                                    existingCommand.id
                                )
                                kord.createGuildChatInputCommand(
                                    guildId = guildId,
                                    name = name,
                                    description = command.description,
                                    builder = command.paramBuilder
                                )
                            }
                        } else {
                            log.info("Found new command ${command.command}, registering")
                            launch {
                                kord.createGuildChatInputCommand(
                                    guildId = guildId,
                                    name = name,
                                    description = command.description,
                                    builder = command.paramBuilder
                                )
                            }
                        }
                    }
                }
                guildRegisteredCommands.forEach { name,command->
                    if (!commandMap.containsKey(name)){
                        log.info("Found outdated command $name, removing")
                        launch {
                            kord.rest.interaction.deleteGuildApplicationCommand(
                                kord.selfId,
                                guildId,
                                command.id
                            )
                        }
                    }
                }
            }

            kord.on<ChatInputCommandInteractionCreateEvent> {
                val guildId = interaction.invokedCommandGuildId!!
                registrarService.syncUser(interaction.user, guildId)
                interaction.respondEphemeral {
                    content = commandMap[interaction.command.rootName]!!.process(interaction)
                }
            }

            log.info("Bot is ready to log in")
            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    private fun commandEqual(guildCmd: GuildApplicationCommand?, cmd: Command): Boolean {
        if (guildCmd == null) return false
        val gcmd = guildCmd.data
        if (gcmd.name != cmd.command) {
            log.info("Command name [${gcmd.name}] differs from [${cmd.command}]")
            return false
        }
        if (gcmd.description != cmd.description) {
            log.info("Command description ${gcmd.description} differs from ${cmd.description} for [${cmd.command}]")
            return false
        }
        val createBuilder = DummyChatInputCreateBuilder()
        cmd.paramBuilder(createBuilder)
        val options =
            createBuilder.options?.map { ComparableOption(it.name, it.description, it.type, it.required ?: false) }
                ?.toSet()
        val existingOptions =
            gcmd.options.value?.map { ComparableOption(it.name, it.description, it.type, it.required.value ?: false) }
                ?.toSet()
        if (options != existingOptions) {
            log.info("Options $options differ from $existingOptions for [${cmd.command}]")
        }
        return options == existingOptions
    }

    data class ComparableOption(
        val name: String,
        val description: String?,
        val type: ApplicationCommandOptionType,
        val isRequired: Boolean
    )

    @PreDestroy
    fun shutdown() = runBlocking {
        job.cancelAndJoin()
    }
}