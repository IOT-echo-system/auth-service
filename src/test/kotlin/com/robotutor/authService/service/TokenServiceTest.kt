package com.robotutor.authService.service

import com.robotutor.authService.builder.TokenBuilder
import com.robotutor.authService.models.IdType
import com.robotutor.authService.repositories.TokenRepository
import com.robotutor.authService.services.TokenService
import com.robotutor.iot.service.IdGeneratorService
import com.robotutor.iot.services.MqttPublisher
import com.robotutor.iot.utils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId

class TokenServiceTest {

    private val tokenRepository = mockk<TokenRepository>()
    private val idGeneratorService = mockk<IdGeneratorService>()
    private val mqttPublisher = mockk<MqttPublisher>()

    private val tokenService = TokenService(tokenRepository = tokenRepository, idGeneratorService = idGeneratorService)
    private val mockTime = LocalDateTime.of(2024, 1, 1, 1, 1)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        mockkStatic(LocalDateTime::class)
        every { mqttPublisher.publish(any(), any()) } just Runs
        every { LocalDateTime.now(ZoneId.of("UTC")) } returns mockTime
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should generate token`() {
        val token =
            TokenBuilder(tokenId = "00001", value = "tokenValue", createdAt = mockTime, expiredAt = mockTime).build()
        every { idGeneratorService.generateId(any()) } returns Mono.just("00001")
        every { tokenRepository.save(any()) } returns Mono.just(token)


        val response = tokenService.generateToken(userId = "001")
            .contextWrite { it.put(MqttPublisher::class.java, mqttPublisher) }

        assertNextWith(response) {
            it shouldBe token
            verify(exactly = 1) {
                idGeneratorService.generateId(IdType.TOKEN_ID)
            }
        }
    }
//
//    @Test
//    fun `should not validate token`() {
//        val tokenValue = "token"
//
//        every { tokenRepository.findByValueAndExpiredAtAfter(any(), any()) } returns Mono.empty()
//
//        val response = tokenService.validate(tokenValue)
//
//        assertErrorWith(response) {
//            it shouldBe UnAuthorizedException(IOTError.IOT0103)
//            verify(exactly = 1) {
//                tokenRepository.findByValueAndExpiredAtAfter(tokenValue, any())
//            }
//        }
//    }
//
//    @Test
//    fun `should not validate token if not exists in db`() {
//        val tokenValue = "token"
//
//        every { tokenRepository.findByValueAndExpiredAtAfter(any(), any()) } returns Mono.empty()
//
//        val response = tokenService.validate(tokenValue)
//
//        assertErrorWith(response) {
//            it shouldBe UnAuthorizedException(IOTError.IOT0103)
//            verify {
//                tokenRepository.findByValueAndExpiredAtAfter(tokenValue, any())
//            }
//        }
//    }
//
//    @Test
//    fun `should reset user password`() {
//        val userDetails = UserDetailsBuilder(userId = "userId").build()
//        val token = TokenBuilder(userId = "userId", otpId = null).build()
//
//        every { tokenRepository.findByValueAndExpiredAtAfter(any(), any()) } returns Mono.just(token)
//        every { userService.resetPassword(any(), any(), any()) } returns Mono.just(userDetails)
//        every { tokenRepository.save(any()) } returns Mono.just(token)
//
//        val response = tokenService.resetPassword(ResetPasswordRequest("password", "new password"), "tokenValue")
//
//        assertNextWith(response) {
//            it shouldBe userDetails
//            verify(exactly = 1) {
//                tokenRepository.findByValueAndExpiredAtAfter("tokenValue", any())
//                userService.resetPassword("userId", "password", "new password")
//                tokenRepository.save(token)
//            }
//        }
//    }
//
//    @Test
//    fun `should not reset user password if current password is not present`() {
//        val token = TokenBuilder(userId = "userId", otpId = null).build()
//
//        every { tokenRepository.findByValueAndExpiredAtAfter(any(), any()) } returns Mono.just(token)
//        val unAuthorizedException = UnAuthorizedException(IOTError.IOT0103)
//        every { userService.resetPassword(any(), any(), any()) } returns Mono.error(unAuthorizedException)
//
//        val response = tokenService.resetPassword(
//            ResetPasswordRequest(currentPassword = null, password = "password"),
//            "tokenValue"
//        )
//
//        assertErrorWith(response) {
//            it shouldBe unAuthorizedException
//            verify(exactly = 1) {
//                tokenRepository.findByValueAndExpiredAtAfter("tokenValue", any())
//                userService.resetPassword("userId", "", "password")
//            }
//        }
//    }
//
//    @Test
//    fun `should reset user password without current password`() {
//        val userDetails = UserDetailsBuilder(userId = "userId").build()
//        val token = TokenBuilder(userId = "userId", otpId = "otpId").build()
//
//        every { tokenRepository.findByValueAndExpiredAtAfter(any(), any()) } returns Mono.just(token)
//        every { userService.resetPassword(any(), any()) } returns Mono.just(userDetails)
//        every { tokenRepository.save(any()) } returns Mono.just(token)
//
//        val response = tokenService.resetPassword(
//            ResetPasswordRequest(currentPassword = null, password = "password"),
//            "tokenValue"
//        )
//
//        assertNextWith(response) {
//            it shouldBe userDetails
//            verify(exactly = 1) {
//                tokenRepository.findByValueAndExpiredAtAfter("tokenValue", any())
//                userService.resetPassword("userId", "password")
//                tokenRepository.save(token)
//            }
//        }
//    }
}
