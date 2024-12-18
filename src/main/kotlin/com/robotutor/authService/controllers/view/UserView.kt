package com.robotutor.authService.controllers.view

import com.robotutor.authService.models.UserId
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AuthLoginRequest(
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class ResetPasswordRequest(
    val currentPassword: String? = null,
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    val password: String
)


data class UpdateTokenRequest(
    @field:NotBlank(message = "ProjectId should not be blank!")
    val projectId: String,
    @field:NotBlank(message = "RoleId should not be blank")
    val roleId: String
)


data class TokenResponse(val token: String)
data class TokenSecretKeyResponse(val secretKey: String)
data class LogoutResponse(val success: Boolean)
data class ResetPasswordResponse(val success: Boolean)

data class ValidateTokenResponse(val userId: UserId, val roleId: String)
data class ValidateDeviceTokenResponse(val userId: UserId, val roleId: String)
data class ValidateOtpTokenResponse(val userId: UserId, val roleId: String)

data class UserPasswordRequest(val userId: UserId, val password: String)
