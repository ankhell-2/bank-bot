package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.Bank
import github.ankhell.bank_bot.jpa.entities.Guild
import github.ankhell.bank_bot.jpa.entities.Member
import github.ankhell.bank_bot.jpa.entities.Transaction
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import kotlinx.datetime.Instant

class TransactionRepositoryCustomImpl(
    @PersistenceContext
    private val entityManager: EntityManager
) : TransactionRepositoryCustom {

    override fun findFiltered(limit: Long, member: Member?, bank: Bank?, guild: Guild): List< Transaction> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Transaction::class.java)
        val root = query.from(Transaction::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates += cb.equal(root.get<Guild>("guild"), guild)

        if (member != null) {
            predicates += cb.equal(root.get<Member>("performedBy"), member)
        }

        if (bank != null) {
            val senderEqual = cb.equal(root.get<Bank>("sender"), bank)
            val receiverEqual = cb.equal(root.get<Bank>("receiver"), bank)
            predicates += cb.or(senderEqual, receiverEqual)
        }

        query.where(cb.and(*predicates.toTypedArray()))
        query.orderBy(cb.desc(root.get<Instant>("timestamp")))

        return entityManager.createQuery(query)
            .setMaxResults(limit.toInt())
            .resultList
    }
}