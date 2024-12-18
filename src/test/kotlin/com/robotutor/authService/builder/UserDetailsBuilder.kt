package com.robotutor.authService.builder

import com.robotutor.authService.models.RoleId
import com.robotutor.authService.models.UserDetails
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class UserDetailsBuilder(
    val id: ObjectId? = null,
    val userId: String = "",
    val password: String = "",
    val registeredAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1),
    val updatedAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1),
    val passwordAttempts: Int = 5,
    val isLocked: Boolean = false,
    val roleId: RoleId = ""
) {
    fun build(): UserDetails {
        return UserDetails(
            id = id,
            userId = userId,
            password = password,
            registeredAt = registeredAt,
            updatedAt = updatedAt,
            passwordAttempts = passwordAttempts,
            isLocked = isLocked,
            roleId = roleId
        )
    }
}
