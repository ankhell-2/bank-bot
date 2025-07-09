package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "spice_run_config")
class SpiceRunConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backing_bank_id", nullable = false)
    var backingBank: Bank = Bank(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_bank_id", nullable = false)
    var mainBank: Bank = Bank(),

    @Column(name = "guild_share", nullable = false)
    var guildShare: Double = 16.67,

    @Column(name = "has_craft_bonus", nullable = false)
    var hasCraftBonus: Boolean = false,

    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)


