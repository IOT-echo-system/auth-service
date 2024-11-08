package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

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
    val userId: UserId,
    val otpId: OtpId? = null,
    val accountId: String? = null,
    val roleId: String? = null,
    val boardId: String? = null
) {
    fun setExpired(): Token {
        this.expiredAt = LocalDateTime.now().minusDays(1)
        return this
    }


    companion object {
        fun generate(
            tokenId: String,
            userId: UserId,
            value: String,
            expiredAt: LocalDateTime,
            otpId: OtpId?,
            accountId: String?,
            roleId: String?,
            boardId: String?
        ): Token {
            return Token(
                tokenId = tokenId,
                userId = userId,
                value = value,
                expiredAt = expiredAt,
                otpId = otpId,
                accountId = accountId,
                roleId = roleId,
                boardId = boardId
            )
        }
    }
}


typealias TokenId = String
