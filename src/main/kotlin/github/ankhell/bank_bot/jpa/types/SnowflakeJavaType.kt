package github.ankhell.bank_bot.jpa.types

import dev.kord.common.entity.Snowflake
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.AbstractClassJavaType

class SnowflakeJavaType : AbstractClassJavaType<Snowflake>(Snowflake::class.java) {

    override fun <X : Any?> unwrap(
        value: Snowflake?,
        type: Class<X>?,
        options: WrapperOptions?
    ): X? {
        if (value == null) return null

        return when {
            type?.isAssignableFrom(Snowflake::class.java) == true -> value as X
            type?.isAssignableFrom(Long::class.java) == true || type == java.lang.Long::class.java -> value.value.toLong() as X
            type?.isAssignableFrom(String::class.java) == true -> value.value.toString() as X
            else -> throw IllegalArgumentException("Unknown unwrap target type: $type")
        }
    }

    override fun <X : Any?> wrap(
        value: X?,
        options: WrapperOptions?
    ): Snowflake? {
        if (value == null) return null

        return when (value) {
            is Snowflake -> value // ✅ just return it
            is Long -> Snowflake(value.toULong())
            is String -> Snowflake(value.toULong())
            else -> throw IllegalArgumentException("Unknown wrap source type: ${value!!::class.java}")
        }
    }
}