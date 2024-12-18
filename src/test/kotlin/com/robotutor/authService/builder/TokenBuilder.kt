package com.robotutor.authService.builder

import com.robotutor.authService.models.RoleId
import com.robotutor.authService.models.Token
import com.robotutor.authService.models.TokenType
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class TokenBuilder(
    val id: ObjectId? = null,
    val tokenId: String = "tokenId",
    val value: String = "token value",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiredAt: LocalDateTime = LocalDateTime.now(),
    val identifier: String = "userID",
    val roleId: RoleId = "otpId",
    val type: TokenType = TokenType.USER,
) {
    fun build(): Token {
        return Token(
            id = id,
            tokenId = tokenId,
            value = value,
            createdAt = createdAt,
            expiredAt = expiredAt, roleId = roleId, type = type, identifier = identifier,

            )
    }
}
