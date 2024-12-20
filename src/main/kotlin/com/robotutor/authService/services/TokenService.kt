package com.robotutor.authService.services

import com.robotutor.authService.controllers.view.ValidateTokenResponse
import com.robotutor.authService.exceptions.IOTError
import com.robotutor.authService.models.IdType
import com.robotutor.authService.models.Token
import com.robotutor.authService.models.TokenType
import com.robotutor.authService.repositories.TokenRepository
import com.robotutor.iot.auditOnSuccess
import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.service.IdGeneratorService
import com.robotutor.iot.utils.createMonoError
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
    private val idGeneratorService: IdGeneratorService,
) {

    fun validate(token: String): Mono<ValidateTokenResponse> {
        return tokenRepository.findByValueAndExpiredAtAfter(token)
            .map {
                when (it.type) {
                    TokenType.USER -> ValidateTokenResponse(userId = it.identifier, roleId = it.roleId)
                    TokenType.OTP -> ValidateTokenResponse(userId = it.identifier, roleId = it.roleId)
                    TokenType.DEVICE -> ValidateTokenResponse(userId = it.identifier, roleId = it.roleId)
                }
            }
            .switchIfEmpty {
                createMonoError(UnAuthorizedException(IOTError.IOT0103))
            }
            .logOnSuccess(message = "Successfully validated token")
            .logOnError(errorCode = IOTError.IOT0103.errorCode, errorMessage = "Failed to validate token")
    }

    fun generateToken(
        identifier: String,
        type: TokenType,
        roleId: String,
        expiredAt: LocalDateTime = LocalDateTime.now().plusDays(7),
    ): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .flatMap { tokenId ->
                tokenRepository.save(
                    Token.generate(
                        tokenId = tokenId,
                        identifier = identifier,
                        expiredAt = expiredAt,
                        roleId = roleId,
                        type = type,
                    )
                )
                    .auditOnSuccess(
                        event = "GENERATE_TOKEN",
                        metadata = mapOf(
                            "tokenId" to tokenId,
                            "identifier" to identifier,
                            "type" to type,
                            "roleId" to roleId
                        ),
                    )
            }
            .logOnSuccess(message = "Successfully generated token")
    }

//    fun resetPassword(resetPasswordRequest: ResetPasswordRequest, tokenValue: String): Mono<UserDetails> {
//        return tokenRepository.findByValueAndExpiredAtAfter(tokenValue)
//            .flatMap { token ->
//                resetPassword(token, resetPasswordRequest)
//                    .logOnSuccess(message = "Successfully reset user password")
//                    .logOnError(errorMessage = "Failed to reset user password")
//                    .flatMap { userDetails ->
//                        tokenRepository.save(token.setExpired())
//                            .map { userDetails }
//                    }
//                    .logOnSuccess(message = "set current token as expired")
//                    .logOnError(errorMessage = "Failed to set current token as expired")
//            }
//    }

    //    private fun resetPassword(token: Token, resetPasswordRequest: ResetPasswordRequest): Mono<UserDetails> {
//        return if (token.otpId != null) {
//            userService.resetPassword(token.userId, resetPasswordRequest.password)
//        } else {
//            userService.resetPassword(
//                token.userId,
//                resetPasswordRequest.currentPassword ?: "",
////                resetPasswordRequest.password
//            )
//        }
//    }
//
//    fun updateToken(updateTokenRequest: UpdateTokenRequest, tokenString: String): Mono<Token> {
//        return tokenRepository.findByValueAndExpiredAtAfter(tokenString)
//            .flatMap { token ->
//                accountServiceGateway.isValidAccountAndRole(
//                    userId = token.userId,
//                    accountId = updateTokenRequest.projectId,
//                    roleId = updateTokenRequest.roleId
//                )
//                    .flatMap {
//                        generateToken(
//                            userId = token.userId,
//                            expiredAt = token.expiredAt,
//                            otpId = null,
//                            accountId = updateTokenRequest.projectId,
//                            roleId = updateTokenRequest.roleId
//                        )
//                    }
//            }
//    }
//
//    fun logout(token: String, userAuthenticationData: UserAuthenticationData): Mono<Token> {
//        return tokenRepository.findByValue(token)
//            .flatMap {
//                tokenRepository.save(it.setExpired())
//            }
//            .auditOnSuccess(event = AuditEvent.LOG_OUT)
//            .auditOnError(event = AuditEvent.LOG_OUT)
//            .logOnSuccess(message = "Successfully logged out user")
//            .logOnError(errorMessage = "Failed to log out user")
//    }
//
//    fun generateTokenForBoard(boardId: String, authenticationData: UserAuthenticationData): Mono<Token> {
//        return tokenRepository.findByBoardIdAndExpiredAtAfter(boardId)
//            .switchIfEmpty {
//                accountServiceGateway.isValidBoard(boardId, authenticationData)
//                    .flatMap {
//                        generateToken(
//                            expiredAt = LocalDateTime.now().plusYears(100),
//                            accountId = authenticationData.accountId,
//                            roleId = "00004",
//                            boardId = boardId,
//                            userId = "Board_$boardId"
//                        )
//                    }
//            }
//    }
//
//    fun updateTokenForBoard(boardId: String, authenticationData: UserAuthenticationData): Mono<Token> {
//        return tokenRepository.deleteByBoardId(boardId)
//            .flatMap {
//                generateTokenForBoard(boardId, authenticationData)
//            }
//    }
}
