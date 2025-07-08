package github.ankhell.bank_bot.jpa.entities

import github.ankhell.bank_bot.Permission
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigInteger

@Entity
data class RolePermission(
    @Id
    val roleID: BigInteger,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id")
    val guild: Guild,

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    val permissions: Set<Permission>
)