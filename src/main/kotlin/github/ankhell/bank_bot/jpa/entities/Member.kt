package github.ankhell.bank_bot.jpa.entities

import dev.kord.common.entity.Snowflake
import github.ankhell.bank_bot.jpa.types.SnowflakeJavaType
import jakarta.persistence.*
import org.hibernate.annotations.JavaType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "members")
class Member(

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var id: Snowflake? = null,

    @Column(unique = true, nullable = false)
    var username: String = "",

    @ElementCollection
    @CollectionTable(name = "member_guild_ids", joinColumns = [JoinColumn(name = "member_id")])
    @Column(name = "guild_id")
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var guildIds: MutableSet<Snowflake> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(name = "member_role_ids", joinColumns = [JoinColumn(name = "member_id")])
    @Column(name = "role_id")
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JavaType(value = SnowflakeJavaType::class)
    var roleIds: MutableSet<Snowflake> = mutableSetOf()
)