package com.robotutor.authService.service

import com.robotutor.authService.builder.UserDetailsBuilder
import com.robotutor.authService.controllers.view.AuthLoginRequest
import com.robotutor.authService.controllers.view.UserPasswordRequest
import com.robotutor.authService.exceptions.IOTError
import com.robotutor.authService.gateway.UserServiceGateway
import com.robotutor.authService.gateway.views.UserIdResponse
import com.robotutor.authService.repositories.UserRepository
import com.robotutor.authService.services.UserService
import com.robotutor.iot.exceptions.BadDataException
import com.robotutor.iot.exceptions.DataNotFoundException
import com.robotutor.iot.services.KafkaPublisher
import com.robotutor.iot.utils.assertErrorWith
import com.robotutor.iot.utils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId

class UserServiceTest {
    private val kafkaPublisher = mockk<KafkaPublisher>()
    private val userRepository = mockk<UserRepository>()
    private val userServiceGateway = mockk<UserServiceGateway>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val userService = UserService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        userServiceGateway = userServiceGateway
    )

    private val mockTime = LocalDateTime.of(2024, 1, 1, 1, 1)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now(ZoneId.of("UTC")) } returns mockTime
        every { kafkaPublisher.publish(any(), any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()

    }

    @Test
    fun `should register a new user with password`() {

        val userDetails = UserPasswordRequest(userId = "001", password = "password")
        val user = UserDetailsBuilder(
            password = "encodedPassword",
            userId = "001",
            registeredAt = mockTime,
            updatedAt = mockTime
        ).build()
        every { LocalDateTime.now() } returns mockTime
        every { userRepository.findByUserId(any()) } returns Mono.empty()
        every { passwordEncoder.encode(any()) } returns "encodedPassword"
        every { userRepository.save(any()) } returns Mono.just(user)

        val response = userService.savePassword(userDetails)

        assertNextWith(response) {
            it shouldBe user
            verify(exactly = 1) {
                userRepository.findByUserId("001")
                userRepository.save(user)
            }
        }
    }

    @Test
    fun `should not register a new user if already exists`() {
        val userDetails = UserPasswordRequest(userId = "userId", password = "password")
        val user = UserDetailsBuilder(password = "encodedPassword", userId = "001").build()

        every { LocalDateTime.now() } returns mockTime
        every { userRepository.findByUserId(any()) } returns Mono.just(user)

        val response = userService.savePassword(userDetails)

        assertErrorWith(response) {
            it shouldBe BadDataException(IOTError.IOT0101)
            verify(exactly = 1) {
                userRepository.findByUserId("userId")
            }
            verify(exactly = 0) {
                userRepository.save(any())
            }
        }
    }

    @Test
    fun `should login user if email and password are correct`() {
        val user = UserDetailsBuilder(password = "encodedPassword", userId = "001").build()
        every { userServiceGateway.getUserId(any()) } returns Mono.just(UserIdResponse("001"))
        every { userRepository.findByUserId(any()) } returns Mono.just(user)
        every { passwordEncoder.matches(any(), any()) } returns true

        val response = userService.login(AuthLoginRequest("email", "password"))
            .contextWrite { it.put(KafkaPublisher::class.java, kafkaPublisher) }

        assertNextWith(response) {
            it shouldBe user
            verify(exactly = 1) {
                userServiceGateway.getUserId("email")
            }
        }
    }

    @Test
    fun `should give mono error if invalid email while verifying credentials`() {
        val userDetails = AuthLoginRequest(email = "email", password = "password")

        every { userServiceGateway.getUserId(any()) } returns Mono.error(DataNotFoundException(IOTError.IOT0102))

        val response = userService.login(userDetails)
            .contextWrite { it.put(KafkaPublisher::class.java, kafkaPublisher) }

        assertErrorWith(response) {
            it shouldBe BadDataException(IOTError.IOT0102)
            verify(exactly = 1) {
                userServiceGateway.getUserId("email")
            }

            verify(exactly = 0) {
                userRepository.findByUserId(any())
            }
        }
    }

    @Test
    fun `should give mono error if invalid password while verifying credentials`() {
        val userDetails = AuthLoginRequest(email = "email", password = "password")
        val userIdResponse = UserIdResponse(userId = "001")
        val user = UserDetailsBuilder(userId = "001", password = "encodedPassword").build()

        every { userServiceGateway.getUserId(any()) } returns Mono.just(userIdResponse)
        every { userRepository.findByUserId(any()) } returns Mono.just(user)
        every { passwordEncoder.matches(any(), any()) } returns false

        val response = userService.login(userDetails)
            .contextWrite { it.put(KafkaPublisher::class.java, kafkaPublisher) }

        assertErrorWith(response) {
            it shouldBe BadDataException(IOTError.IOT0102)
            verify(exactly = 1) {
                userServiceGateway.getUserId("email")
                userRepository.findByUserId("001")
                passwordEncoder.matches("password", "encodedPassword")
            }
        }
    }
//
//    @Test
//    fun `should get user by email`() {
//        val user = UserDetailsBuilder().build()
//        every { userRepository.findByEmail(any()) } returns Mono.just(user)
//
//        val response = userService.getUserByEmail("example@email.com")
//
//        assertNextWith(response) {
//            it shouldBe user
//
//            verify(exactly = 1) {
//                userRepository.findByEmail("example@email.com")
//            }
//        }
//    }
//
//    @Test
//    fun `should give error if user not exist by email`() {
//        every { userRepository.findByEmail(any()) } returns Mono.empty()
//
//        val response = userService.getUserByEmail("example@email.com")
//
//        assertErrorWith(response) {
//            it shouldBe DataNotFoundException(IOTError.IOT0106)
//
//            verify(exactly = 1) {
//                userRepository.findByEmail("example@email.com")
//            }
//        }
//    }
//
//    @Test
//    fun `should reset user password`() {
//        val userDetails =
//            UserDetailsBuilder(userId = "userId", email = "example@email.com", password = "encodedPassword").build()
//        every { userRepository.findByUserId(any()) } returns Mono.just(userDetails)
//        every { userRepository.findByEmail(any()) } returns Mono.just(userDetails)
//        every { passwordEncoder.matches(any(), any()) } returns true
//        every { userRepository.save(any()) } returns Mono.just(userDetails)
//        every { passwordEncoder.encode(any()) } returns "newEncodedPassword"
//
//        val response = userService.resetPassword("userId", currentPassword = "password", password = "new password")
//
//        assertNextWith(response) {
//            it shouldBe userDetails
//
//            verify(exactly = 1) {
//                userRepository.findByUserId("userId")
//                userRepository.findByEmail("example@email.com")
//                passwordEncoder.matches("password", "encodedPassword")
//                passwordEncoder.encode("new password")
//                userRepository.save(userDetails.copy(password = "newEncodedPassword"))
//            }
//        }
//    }
//
//    @Test
//    fun `should not reset user password if current password is not valid`() {
//        val userDetails =
//            UserDetailsBuilder(userId = "userId", email = "example@email.com", password = "encodedPassword").build()
//        every { userRepository.findByUserId(any()) } returns Mono.just(userDetails)
//        every { userRepository.findByEmail(any()) } returns Mono.just(userDetails)
//        every { passwordEncoder.matches(any(), any()) } returns false
//
//        val response = userService.resetPassword("userId", password = "new password", currentPassword = "")
//
//        assertErrorWith(response) {
//            it shouldBe BadDataException(IOTError.IOT0102)
//
//            verify(exactly = 1) {
//                userRepository.findByUserId("userId")
//                userRepository.findByEmail("example@email.com")
//                passwordEncoder.matches("", "encodedPassword")
//            }
//        }
//    }
//
//    @Test
//    fun `should reset user password with otp`() {
//        val userDetails =
//            UserDetailsBuilder(userId = "userId", email = "example@email.com", password = "encodedPassword").build()
//        every { userRepository.findByUserId(any()) } returns Mono.just(userDetails)
//        every { userRepository.save(any()) } returns Mono.just(userDetails)
//        every { passwordEncoder.encode(any()) } returns "encodedPassword"
//
//        val response = userService.resetPassword("userId", password = "password")
//
//        assertNextWith(response) {
//            it shouldBe userDetails
//
//            verify(exactly = 1) {
//                userRepository.findByUserId("userId")
//                passwordEncoder.encode("password")
//                userRepository.save(userDetails.copy(password = "encodedPassword"))
//            }
//        }
//    }
}
