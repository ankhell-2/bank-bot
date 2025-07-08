package github.ankhell.bank_bot.services

import github.ankhell.bank_bot.converters.toBigInteger
import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.repositories.BalanceRepository
import github.ankhell.bank_bot.jpa.repositories.BankRepository
import github.ankhell.bank_bot.service.BanksService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.util.*
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BanksServiceTest {

    private lateinit var bankRepository: BankRepository
    private lateinit var balanceRepository: BalanceRepository
    private lateinit var banksService: BanksService

    private val guild = Guild(id = Random.nextULong().toBigInteger())
    private val bank = Bank(uuid = UUID.randomUUID(), shortName = "TB", fullName = "Test Bank", guild = guild)

    @BeforeEach
    fun setUp() {
        bankRepository = mockk()
        balanceRepository = mockk()
        banksService = BanksService(bankRepository, balanceRepository)
    }

    @Test
    fun `should add bank successfully`() = runTest {
        every { bankRepository.findByGuildAndShortName(guild, "TB") } returns null
        every { bankRepository.save(any()) } returns bank

        val result = banksService.addBank("TB", "Test Bank", guild)

        assertEquals("Bank (TB) - Test Bank successfully added", result)
        verify { bankRepository.save(match { it.shortName == "TB" && it.fullName == "Test Bank" }) }
    }

    @Test
    fun `should not add bank if it exists`() = runTest {
        every { bankRepository.findByGuildAndShortName(guild, "TB") } returns bank

        val result = banksService.addBank("TB", "Test Bank", guild)

        assertEquals("Bank with short name (TB) already exist in that guild!", result)
        verify(exactly = 0) { bankRepository.save(any()) }
    }

    @Test
    fun `should list all banks by guild`() = runTest {
        every { bankRepository.findAllByGuild(guild) } returns setOf(bank)

        val result = banksService.listAllByGuild(guild)

        assertEquals(1, result.size)
        assertTrue(result.contains(bank))
    }

    @Test
    fun `should modify bank if exists`() = runTest {
        val modifiedBank = bank.copy(fullName = "New Name")
        every { bankRepository.findByIdOrNull(bank.uuid!!) } returns bank
        every { bankRepository.save(any()) } returns modifiedBank

        val result = banksService.modifyBank(bank.uuid!!, null, "New Name")

        assertEquals("Bank New Name modified successfully", result)
        verify { bankRepository.save(match { it.fullName == "New Name" }) }
    }

    @Test
    fun `should not modify bank if not found`() = runTest {
        val uuid = UUID.randomUUID()
        every { bankRepository.findByIdOrNull(uuid) } returns null

        val result = banksService.modifyBank(uuid, "X", "Y")

        assertEquals("Bank with uuid $uuid doesn't exist", result)
    }

    @Test
    fun `should remove bank and mark balance as deleted if exists`() = runTest {
        every { bankRepository.findByGuildAndShortName(guild, "TB") } returns bank
        every { balanceRepository.findByBank(bank) } returns mockk(relaxed = true)
        every { balanceRepository.save(any()) } returns mockk()
        every { bankRepository.save(any()) } returns mockk()

        val result = banksService.removeBank("TB", guild)

        assertEquals("Bank (TB) removed successfully", result)
    }

    @Test
    fun `should return not exist message if bank not found`() = runTest {
        every { bankRepository.findByGuildAndShortName(guild, "XXX") } returns null

        val result = banksService.removeBank("XXX", guild)

        assertEquals("Bank (XXX) doesn't exist", result)
    }
}
