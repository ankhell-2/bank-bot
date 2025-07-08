package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionRepository: JpaRepository<Transaction, UUID>