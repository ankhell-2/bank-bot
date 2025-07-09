package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "spice_run")
class SpiceRun(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "spice_run_participants",
        joinColumns = [JoinColumn(name = "spice_run_id")],
        inverseJoinColumns = [JoinColumn(name = "spice_miner_uuid")]
    )
    var participants: MutableSet<SpiceMiner> = mutableSetOf(),

    @Column(name = "spice_gathered", nullable = false)
    var spiceGathered: Long = 50_000L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spice_run_config_id", nullable = false)
    var config: SpiceRunConfig,

    @Column(name = "guild_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildId: Snowflake = Snowflake(0uL)

)