package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigInteger
import java.util.UUID

@Entity
@Table(name = "balances")
data class Balance(

    @Id
    val bankId: UUID?=null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "bank_id")
    val bank: Bank,

    @Column(nullable = false)
    val amount: BigInteger,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id")
    val guild: Guild
)