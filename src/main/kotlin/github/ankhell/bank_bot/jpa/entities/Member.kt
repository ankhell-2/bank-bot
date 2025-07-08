package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.math.BigInteger

@Entity
@Table(name = "members")
data class Member(

    @Id
    val id: BigInteger? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @ManyToMany
    @JoinTable(
        name = "user_guild",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "guild_id")]
    )
    val guilds: Set<Guild> = emptySet(),

    val roleIds: Set<BigInteger>
)