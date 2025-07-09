package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(
    name = "spice_miners",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["guild_id", "name"])
    ]
)
class SpiceMiner(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var uuid: UUID? = null,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var debt: Long = 0,
    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)
