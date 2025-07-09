package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "transactions")
class Transaction(

    @Id
    @GeneratedValue
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_id")
    var sender: Bank? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_bank_id")
    var receiver: Bank? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by")
    var performedBy: Member = Member(),

    @Column(nullable = false)
    var amount: Long = 0,

    @Column(nullable = false)
    var comment: String = "",

    @Column(nullable = false)
    var timestamp: Instant = Clock.System.now().toJavaInstant(),

    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)