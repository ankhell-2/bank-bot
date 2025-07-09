package github.ankhell.bank_bot.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import github.ankhell.bank_bot.jpa.entities.*
import github.ankhell.bank_bot.jpa.repositories.*
import github.ankhell.bank_bot.service.MemberService
import github.ankhell.bank_bot.service.TransactionService
import github.ankhell.bank_bot.table.AsciiTransactionTableRenderer
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.math.BigInteger
import java.util.*
import kotlin.test.assertEquals

class TransactionServiceTest {

    private val balanceRepository = mockk<BalanceRepository>(relaxed = true)
    private val bankRepository = mockk<BankRepository>(relaxed = true)
    private val transactionRepository = mockk<TransactionRepository>(relaxed = true)
    private val registrar = mockk<MemberService>(relaxed = true)

    private val service = TransactionService(
        balanceRepository,
        bankRepository,
        transactionRepository,
        registrar,
        AsciiTransactionTableRenderer()
    )

    private val user = mockk<User>()
    private val member = Member(id = Snowflake(1uL), username = "testuser")
    private val guildId = Snowflake(1uL)

    private val senderBank = Bank(uuid = UUID.randomUUID(), shortName = "SND", fullName = "SenderBank", guildId = guildId)
    private val receiverBank = Bank(uuid = UUID.randomUUID(), shortName = "RCV", fullName = "ReceiverBank", guildId = guildId)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        coEvery { registrar.getUser(user) } returns member
    }

    @Test
    fun `should return error if both sender and receiver are null`() = runTest {
        val result = service.performTransaction(user, null, null, guildId, 100, "test")
        assertEquals("At least one of sender/receiver pair should be present", result)
    }

    @Test
    fun `should return error if sender bank is not found`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "SND") } returns null

        val result = service.performTransaction(user, "SND", null, guildId, 100, "test")

        assertEquals("Bank (SND) not found", result)
    }

    @Test
    fun `should return error if receiver bank is not found`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "RCV") } returns null

        val result = service.performTransaction(user, null, "RCV", guildId, 100, "test")

        assertEquals("Bank (RCV) not found", result)
    }

    @Test
    fun `should rollback if sender has insufficient balance`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "SND") } returns senderBank
        coEvery { transactionRepository.save(any()) } returns mockk()
        coEvery { balanceRepository.findByBank(senderBank) } returns Balance(
            bank = senderBank,
            amount = BigInteger.valueOf(50),
            guildId = guildId
        )

        val status = mockk<TransactionStatus>(relaxed = true)

        // 🔧 Correct mocking of Java static method
        mockkStatic(TransactionAspectSupport::class)
        every { TransactionAspectSupport.currentTransactionStatus() } returns status

        val result = service.performTransaction(user, "SND", null, guildId, 100, "test")

        assertEquals("Sender bank (SND) has insufficient balance to perform the transaction", result)
        verify { status.setRollbackOnly() }

        unmockkStatic(TransactionAspectSupport::class)
    }

    @Test
    fun `should succeed topping up receiver`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "RCV") } returns receiverBank
        coEvery { balanceRepository.findByBank(receiverBank) } returns null
        coEvery { transactionRepository.save(any()) } returns mockk<Transaction>()

        val savedBalance = Balance(
            bank = receiverBank,
            amount = BigInteger.valueOf(100),
            guildId = guildId,
            isDeleted = false
        )
        coEvery { balanceRepository.save(any()) } returns savedBalance // ✅ fixed

        val result = service.performTransaction(user, null, "RCV", guildId, 100, "test")

        assertEquals("Successfully topped up bank ReceiverBank for 100", result)
    }

    @Test
    fun `should succeed withdrawing from sender`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "SND") } returns senderBank

        val existingBalance = Balance(
            bank = senderBank,
            amount = BigInteger.valueOf(200),
            guildId = guildId,
            isDeleted = false
        )
        coEvery { balanceRepository.findByBank(senderBank) } returns existingBalance
        coEvery { transactionRepository.save(any()) } returns mockk<Transaction>()
        coEvery { balanceRepository.save(any()) } returns
            Balance(
                bank = existingBalance.bank,
                amount = BigInteger.valueOf(100),
                guildId = existingBalance.guildId,
                isDeleted = existingBalance.isDeleted
            )

        val result = service.performTransaction(user, "SND", null, guildId, 100, "test")

        assertEquals("Successfully withdrawn 100 from SenderBank", result)
    }

    @Test
    fun `should succeed full transfer from sender to receiver`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "SND") } returns senderBank
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "RCV") } returns receiverBank

        val senderBalance = Balance(
            bank = senderBank,
            amount = BigInteger.valueOf(200),
            guildId = guildId,
            isDeleted = false
        )
        coEvery { balanceRepository.findByBank(senderBank) } returns senderBalance

        val receiverInitial = null
        val receiverFinal = Balance(
            bank = receiverBank,
            amount = BigInteger.valueOf(100),
            guildId = guildId,
            isDeleted = false
        )
        coEvery { balanceRepository.findByBank(receiverBank) } returns receiverInitial
        coEvery { transactionRepository.save(any()) } returns mockk<Transaction>()
        coEvery { balanceRepository.save(any()) } returns
                Balance(
                    bank = senderBalance.bank,
                    amount = BigInteger.valueOf(100),
                    guildId = senderBalance.guildId,
                    isDeleted = senderBalance.isDeleted
                )
        coEvery { balanceRepository.save(match { it.bank == receiverBank }) } returns receiverFinal

        val result = service.performTransaction(user, "SND", "RCV", guildId, 100, "test")

        assertEquals("Successfully transferred 100 from SenderBank to ReceiverBank", result)
    }

    @Test
    fun `getBalances should return all balances if abbreviation is null`() = runTest {
        val balances = setOf(
            Balance(
                bank = senderBank,
                amount = BigInteger.ONE,
                guildId = guildId,
                isDeleted = false
            )
        )
        coEvery { balanceRepository.findAllByGuildId(guildId) } returns balances

        val result = service.getBalances(guildId, null)

        assertEquals(balances, result)
    }

    @Test
    fun `getBalances should return empty set if bank not found`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "XYZ") } returns null

        val result = service.getBalances(guildId, "XYZ")

        assertEquals(emptySet(), result)
    }

    @Test
    fun `getBalances should return set with single balance`() = runTest {
        coEvery { bankRepository.findByGuildIdAndShortName(guildId, "RCV") } returns receiverBank

        val expectedBalance = Balance(
            bank = receiverBank,
            amount = BigInteger.TEN,
            guildId = guildId,
            isDeleted = false
        )
        coEvery { balanceRepository.findByBank(receiverBank) } returns expectedBalance

        val result = service.getBalances(guildId, "RCV")

        assertEquals(1, result.size)
        assertEquals(expectedBalance, result.first())
    }
}
