package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.Where
import java.util.UUID

@Entity
@Table(
    name = "banks",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["guild_id", "short_name"])
    ]
)
@SQLRestriction("is_deleted = false")
data class Bank(

    @Id
    @GeneratedValue
    val uuid: UUID? = null,

    @Column(name = "short_name", nullable = false)
    val shortName: String, // e.g. "HSBC"

    @Column(nullable = false)
    val fullName: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id")
    val guild: Guild,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false
)