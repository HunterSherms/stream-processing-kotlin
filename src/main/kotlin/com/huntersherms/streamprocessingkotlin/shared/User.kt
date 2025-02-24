package com.huntersherms.streamprocessingkotlin.shared

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import java.time.Instant
import java.time.LocalDate

/**
 * A generic user data class
 */
@Serializable
data class User(
    val userId: String,
    val name: String,
    val email: String,
    @Serializable(with = LocalDateSerializer::class) val dob: LocalDate,
    @Serializable(with = InstantSerializer::class) val accountCreated: Instant
)

fun User.toCleanedUser(): CleanedUser = CleanedUser(
    userId = this.userId,
    accountCreated = this.accountCreated
)

/**
 * A [User] with PII data stripped out
 */
@Serializable
data class CleanedUser(
    val userId: String,
    @Serializable(with = InstantSerializer::class) val accountCreated: Instant
)

// Kafka Serde implementation for User
class UserSerde : Serde<User> {
    override fun serializer(): Serializer<User> = Serializer { _, data ->
        Json.encodeToString(data).toByteArray()
    }

    override fun deserializer(): Deserializer<User> = Deserializer { _, data ->
        Json.decodeFromString<User>(String(data))
    }
}

// Kafka Serde implementation for CleanedUser
class CleanedUserSerde : Serde<CleanedUser> {
    override fun serializer(): Serializer<CleanedUser> = Serializer { _, data ->
        Json.encodeToString(data).toByteArray()
    }

    override fun deserializer(): Deserializer<CleanedUser> = Deserializer { _, data ->
        Json.decodeFromString<CleanedUser>(String(data))
    }
}
