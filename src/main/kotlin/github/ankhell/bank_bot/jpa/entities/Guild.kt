package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.math.BigInteger

@Entity
data class Guild(
    @Id
    val id: BigInteger
)
