package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "type", column = Column(name = "sender_type")),
        AttributeOverride(name = "bankId", column = Column(name = "sender_bank_id")),
        AttributeOverride(name = "userId", column = Column(name = "sender_user_id"))
    )
    val sender: TransactionParty,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "type", column = Column(name = "receiver_type")),
        AttributeOverride(name = "bankId", column = Column(name = "receiver_bank_id")),
        AttributeOverride(name = "userId", column = Column(name = "receiver_user_id"))
    )
    val receiver: TransactionParty,

    @Column(nullable = false)
    val amount: Long,

    val timestamp: java.time.Instant = Clock.System.now().toJavaInstant(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id")
    val guild: Guild
)