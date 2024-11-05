package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val USER_COLLECTION = "users"

@TypeAlias("User")
@Document(USER_COLLECTION)
data class UserDetails(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: UserId,
    var password: String,
    val registeredAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    val passwordAttempts: Int = 5,
    val isLocked: Boolean = false
) {
    fun updatePassword(encodedPassword: String): UserDetails {
        this.password = encodedPassword
        this.updatedAt = LocalDateTime.now()
        return this
    }
}

typealias UserId = String
