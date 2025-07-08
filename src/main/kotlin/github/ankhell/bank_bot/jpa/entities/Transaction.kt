package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "transactions")
data class Transaction(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_id")
    val sender: Bank? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_bank_id")
    val receiver: Bank? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by")
    val performedBy: Member,

    @Column(nullable = false)
    val amount: Long,

    @Column(nullable = false)
    val comment: String,

    @Column(nullable = false)
    val timestamp: Instant = Clock.System.now().toJavaInstant(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id")
    val guild: Guild
)