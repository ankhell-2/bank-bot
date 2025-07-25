package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import java.math.BigInteger
import java.util.*

@Entity
@Table(name = "balances")
@SQLRestriction("is_deleted = false")
class Balance(
    @Id
    var bankId: UUID? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "bank_id")
    var bank: Bank = Bank(),

    @Column(nullable = false)
    var amount: BigInteger,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)