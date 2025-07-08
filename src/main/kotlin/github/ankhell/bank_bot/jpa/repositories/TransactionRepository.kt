package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.entities.Transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionRepository: JpaRepository<Transaction, UUID>, TransactionRepositoryCustom{

    fun findAllBySenderOrReceiver(sender: Bank, receiver: Bank, pageable: Pageable): List<Transaction>

    fun findAllByPerformedBy(member: Member, pageable: Pageable): List<Transaction>

    fun findAllByOrderByTimestampDesc(pageable: Pageable): List<Transaction>
}