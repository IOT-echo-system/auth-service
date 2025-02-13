package com.robotutor.authService.services

import com.robotutor.authService.controllers.view.AuthLoginRequest
import com.robotutor.authService.controllers.view.UserPasswordRequest
import com.robotutor.authService.exceptions.IOTError
import com.robotutor.authService.gateway.UserServiceGateway
import com.robotutor.authService.models.UserDetails
import com.robotutor.authService.repositories.UserRepository
import com.robotutor.iot.auditOnError
import com.robotutor.iot.auditOnSuccess
import com.robotutor.iot.exceptions.BadDataException
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userServiceGateway: UserServiceGateway
) {
    private val logger = Logger(this::class.java)
//    fun resetPassword(userId: UserId, password: String): Mono<UserDetails> {
//        return userRepository.findByUserId(userId)
//            .flatMap {
//                userRepository.save(it.updatePassword(passwordEncoder.encode(password)))
//            }
//            .auditOnSuccess(event = AuditEvent.RESET_PASSWORD, userId = userId)
//            .auditOnError(event = AuditEvent.RESET_PASSWORD, userId = userId)
//            .logOnSuccess(message = "Successfully updated password")
//            .logOnError(errorMessage = "Failed to update password")
//    }

//    fun resetPassword(userId: UserId, currentPassword: String, password: String): Mono<UserDetails> {
//        return userRepository.findByUserId(userId)
//            .flatMap {
////                this.verifyCredentials(UserLoginRequest(email = it.email, password = currentPassword))
//            }
//            .flatMap {
//                userRepository.save(it.updatePassword(passwordEncoder.encode(password)))
//            }
//            .auditOnSuccess(event = AuditEvent.RESET_PASSWORD, userId = userId)
//            .auditOnError(event = AuditEvent.RESET_PASSWORD, userId = userId)
//            .logOnSuccess(message = "Successfully updated password")
//            .logOnError(errorMessage = "Failed to update password")
//    }

    fun savePassword(user: UserPasswordRequest): Mono<UserDetails> {
        return userRepository.findByUserId(user.userId)
            .flatMap {
                createMonoError<UserDetails>(BadDataException(IOTError.IOT0101))
            }
            .switchIfEmpty {
                val userDetails = UserDetails(
                    userId = user.userId,
                    password = passwordEncoder.encode(user.password),
                    roleId = "" // TODO: Update the role id here with default one.
                )
                userRepository.save(userDetails)
            }
            .logOnSuccess(
                logger,
                "Successfully registered new user",
                additionalDetails = mapOf("userId" to user.userId)
            )
            .logOnError(
                logger,
                errorMessage = "Failed to register new User",
                additionalDetails = mapOf("userId" to user.userId)
            )
            .auditOnSuccess("SAVE_PASSWORD", userId = user.userId)
    }

    fun login(userDetails: AuthLoginRequest): Mono<UserDetails> {
        return userServiceGateway.getUserId(userDetails.email)
            .onErrorResume { createMonoError(BadDataException(IOTError.IOT0102)) }
            .flatMap { userRepository.findByUserId(it.userId) }
            .flatMap { details ->
                val matches = passwordEncoder.matches(userDetails.password, details.password)
                if (matches) {
                    createMono(details)
                        .auditOnSuccess(event = "VERIFY_PASSWORD", userId = details.userId)
                } else {
                    createMonoError<UserDetails>(BadDataException(IOTError.IOT0102))
                        .auditOnError(event = "VERIFY_PASSWORD", userId = details.userId)
                }
            }
            .logOnSuccess(logger, "Successfully verified password")
            .logOnError(logger, "", "Failed to verify password")
    }
}

