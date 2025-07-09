package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(
    name = "banks",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["guild_id", "short_name"])
    ]
)
@SQLRestriction("is_deleted = false")
class Bank(

    @Id
    @GeneratedValue
    var uuid: UUID? = null,

    @Column(name = "short_name", nullable = false)
    var shortName: String = "", // e.g. "HSBC"

    @Column(name = "full_name", nullable = false)
    var fullName: String = "",

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)