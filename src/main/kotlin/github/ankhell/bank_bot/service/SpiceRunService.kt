package github.ankhell.bank_bot.service

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.Result
import github.ankhell.bank_bot.jpa.entities.SpiceMiner
import github.ankhell.bank_bot.jpa.entities.SpiceRun
import github.ankhell.bank_bot.jpa.entities.SpiceRunConfig
import github.ankhell.bank_bot.jpa.repositories.SpiceMinerRepository
import github.ankhell.bank_bot.jpa.repositories.SpiceRunConfigRepository
import github.ankhell.bank_bot.jpa.repositories.SpiceRunRepository
import github.ankhell.bank_bot.table.MinersTableRenderer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

private const val GUILD_MINER_ID = "guild"

private const val MELANGE_DISCOUNTED_RATE = 7500

private const val MELANGE_NORMAL_RATE = 10_000

private const val MELANGE_PER_REF_RUN = 200

@Service
class SpiceRunService(
    private val spiceMinerRepository: SpiceMinerRepository,
    private val spiceRunRepository: SpiceRunRepository,
    private val spiceRunConfigRepository: SpiceRunConfigRepository,
    private val banksService: BanksService,
    private val transactionService: TransactionService,
    private val minersTableRenderer: MinersTableRenderer
) {

    @Transactional
    suspend fun saveConfig(
        backingBank: String,
        mainBank: String,
        guildShare: Double,
        hasCraftBonus: Boolean,
        guildId: Snowflake
    ): Result {
        val bckBank = banksService.getBank(backingBank, guildId)
        if (bckBank == null) {
            return Result.failure("Bank $backingBank not found")
        }
        val mnBank = banksService.getBank(mainBank, guildId)
        if (mnBank == null) {
            return Result.failure("Bank $backingBank not found")
        }
        val config = SpiceRunConfig(
            mainBank = mnBank,
            backingBank = bckBank,
            guildShare = guildShare,
            hasCraftBonus = hasCraftBonus,
            guildId = guildId
        )
        spiceRunConfigRepository.save(config)
        return Result.success("Configuration successfully saved")
    }

    @Transactional
    suspend fun registerRun(
        miners: Set<String>,
        guildId: Snowflake,
        amount: Long = 50_000
    ): Result {
        val config = spiceRunConfigRepository.findTopByGuildIdOrderByIdDesc(guildId)
        if (config == null) {
            return Result.failure("Configuration for spice runs not found, please add one using /spicerun_configure")
        }

        val melangePiecePrice =
            (if (config.hasCraftBonus) MELANGE_DISCOUNTED_RATE else MELANGE_NORMAL_RATE) / MELANGE_PER_REF_RUN
        val melange = amount / melangePiecePrice

        val guildMiner =
            spiceMinerRepository.findByGuildIdAndName(guildId, GUILD_MINER_ID) ?: SpiceMiner(
                name = GUILD_MINER_ID,
                guildId = guildId
            )
        val melangeGuildShare = (melange * (config.guildShare / 100)).toLong()
        guildMiner.debt += melangeGuildShare
        spiceMinerRepository.save(guildMiner)

        val melangeForMiners = melange - melangeGuildShare
        val minerPay = melangeForMiners / miners.size

        val spiceMiners = miners.map {
            val miner =
                spiceMinerRepository.findByGuildIdAndName(guildId, it) ?: SpiceMiner(name = it, guildId = guildId)
            miner.debt += minerPay
            spiceMinerRepository.save(miner)
        }
        val spiceRun = SpiceRun(
            participants = spiceMiners.toMutableSet(),
            spiceGathered = amount,
            config = config,
            guildId = guildId
        )
        spiceRunRepository.save(spiceRun)
        return Result.success("Spice run registered\nGuild share: $melangeGuildShare\nMiners payment: $minerPay")
    }

    @Transactional
    suspend fun rollbackRun(guildId: Snowflake, id: Long?): Result {
        val spiceRun = if (id != null) {
            spiceRunRepository.findByIdAndGuildId(id, guildId)
        } else {
            spiceRunRepository.findTopByGuildIdOrderByIdDesc(guildId)
        }

        val totalMelange = spiceRun.spiceGathered
        val config = spiceRun.config

        val melangePiecePrice =
            (if (config.hasCraftBonus) MELANGE_DISCOUNTED_RATE else MELANGE_NORMAL_RATE) / MELANGE_PER_REF_RUN
        val melange = totalMelange / melangePiecePrice

        val melangeGuildShare = (melange * (config.guildShare / 100)).toLong()
        val melangeForMiners = melange - melangeGuildShare
        val minerPay = melangeForMiners / spiceRun.participants.size

        // Rollback guild miner
        val guildMiner = spiceMinerRepository.findByGuildIdAndName(guildId, GUILD_MINER_ID)
        if (guildMiner != null) {
            guildMiner.debt -= melangeGuildShare
            spiceMinerRepository.save(guildMiner)
        }

        // Rollback individual miners
        for (miner in spiceRun.participants) {
            val existingMiner = spiceMinerRepository.findByGuildIdAndName(guildId, miner.name)
            if (existingMiner != null) {
                existingMiner.debt -= minerPay
                spiceMinerRepository.save(existingMiner)
            }
        }

        spiceRunRepository.delete(spiceRun)

        return Result.success("Spice run has been rolled back.")
    }

    @Transactional
    suspend fun getMiners(guildId: Snowflake): Result {
        val miners = spiceMinerRepository.findAllByGuildId(guildId)
        return Result.success(minersTableRenderer.render(miners))
    }

    @Transactional
    suspend fun pay(minerName: String, amount: Long, guildId: Snowflake, user: User): Result {
        val miner = spiceMinerRepository.findByGuildIdAndName(guildId, minerName)
        if (miner == null) {
            return Result.failure("Miner $minerName not found!")
        }
        val config = spiceRunConfigRepository.findTopByGuildIdOrderByIdDesc(guildId)
        if (config == null) {
            return Result.failure("Configuration for spice runs not found, please add one using /spicerun_configure")
        }
        if (miner.debt<amount) {
            return Result.failure("Can't pay more than a current debt")
        }
        if (miner.name == GUILD_MINER_ID) {
            miner.debt -= amount
            spiceMinerRepository.save(miner)
            val transactionResult = transactionService.performTransactionWithResult(
                user = user,
                sender = config.backingBank.shortName,
                receiver = config.mainBank.shortName,
                guildId = guildId,
                amount = amount,
                comment = "Guild spice mining share transfer",
            )
            if (transactionResult.isFailure) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return transactionResult
            }
            return Result.success("Successfully transferred $amount from ${config.backingBank.fullName} to ${config.mainBank.fullName}")
        } else {
            miner.debt -= amount
            spiceMinerRepository.save(miner)
            val transactionResult = transactionService.performTransactionWithResult(
                user = user,
                sender = config.backingBank.shortName,
                guildId = guildId,
                amount = amount,
                comment = "Spice miner ${miner.name} payment",
            )
            if (transactionResult.isFailure) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return transactionResult
            }
            return Result.success("Successfully paid $amount to $minerName")
        }
    }
}