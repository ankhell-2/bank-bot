package github.ankhell.bank_bot.jpa.entities

import jakarta.persistence.Embeddable
import java.math.BigInteger
import java.util.UUID

@Embeddable
data class TransactionParty(
    val type: PartyType,

    val bankId: UUID? = null,  // when type = BANK

    val userId: BigInteger? = null     // when type = USER
)

enum class PartyType {
    BANK, USER
}