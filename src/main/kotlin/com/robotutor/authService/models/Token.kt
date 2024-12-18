package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneOffset

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("Token")
@Document(TOKEN_COLLECTION)
data class Token(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val tokenId: TokenId,
    val value: String,
    @Indexed(name = "sessionExpiryIndex", expireAfterSeconds = 604800)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var expiredAt: LocalDateTime,
    val roleId: String,
    val type: TokenType,
    val identifier: String
) {
    fun setExpired(): Token {
        this.expiredAt = LocalDateTime.now().minusDays(1)
        return this
    }


    companion object {
        fun generate(
            tokenId: String,
            expiredAt: LocalDateTime,
            roleId: String,
            type: TokenType,
            identifier: String
        ): Token {
            val value = "RTT_" + generateTokenValue(length = if (type == TokenType.DEVICE) 32 else 80)
            return Token(
                tokenId = tokenId,
                value = value,
                expiredAt = expiredAt,
                roleId = roleId,
                type = type,
                identifier = identifier
            )
        }

        private fun generateTokenValue(length: Int = 120): String {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
            val token = List(length + 10) { chars.random() }.joinToString("").substring(0, length)
            return token + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
        }
    }
}


typealias TokenId = String

enum class TokenType {
    USER,
    DEVICE,
    OTP
}
