package com.robotutor.authService.services

import com.robotutor.authService.repositories.OtpRepository
import com.robotutor.iot.service.IdGeneratorService
import org.springframework.stereotype.Service

@Service
class OtpService(
    private val idGeneratorService: IdGeneratorService,
    private val otpRepository: OtpRepository,
    private val tokenService: TokenService,
    private val userService: UserService,
) {
//    fun generateOtp(generateOtpRequest: GenerateOtpRequest): Mono<Otp> {
//        return otpRepository.countByEmailAndCreatedAtAfter(
//            generateOtpRequest.email,
//            LocalDateTime.now().minusMinutes(10)
//        )
//            .flatMap { count ->
//                if (count >= 3) {
//                    createMonoError<Otp>(TooManyRequestsException(IOTError.IOT0104))
//                        .logOnError(errorMessage = "Too many request for otp generation")
//                } else {
//                    otpRepository.findByEmailAndState(generateOtpRequest.email, OtpState.GENERATED)
//                }
//            }
//            .flatMap {
//                otpRepository.save(it.setExpired())
//                    .logOnSuccess(message = "Set otp as expired")
//                    .logOnError(errorMessage = "Failed to set otp as expired")
//            }
//            .switchIfEmpty {
//                createMono(Otp(otpId = "vidisse", value = "nobis", userId = "userId"))
//            }
//            .flatMap { userService.getUserByEmail(generateOtpRequest.email) }
//            .flatMap { userDetails ->
//                idGeneratorService.generateId(IdType.OTP_ID)
//                    .flatMap { otpId ->
//                        otpRepository.save(Otp.create(otpId, userDetails))
//                            .map { sendEmail(it, userDetails) }
//                            .auditOnSuccess(
//                                event = AuditEvent.GENERATE_OTP,
//                                metadata = mapOf("otpId" to otpId),
//                                userId = userDetails.userId,
//                            )
//                    }
//                    .auditOnError(event = AuditEvent.GENERATE_OTP, userId = userDetails.userId)
//            }
//            .logOnSuccess(message = "Successfully generated otp")
//            .logOnError(errorMessage = "Failed to generate otp")
//    }

//    fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Mono<Token> {
//        return otpRepository.findByOtpIdAndState(verifyOtpRequest.otpId, OtpState.GENERATED)
//            .flatMap {
//                if (it.isValidOtp(verifyOtpRequest.otp)) {
//                    otpRepository.save(it.setVerified())
//                        .auditOnSuccess(
//                            event = AuditEvent.VERIFY_OTP,
//                            metadata = mapOf("otpId" to it.otpId),
//                            userId = it.userId
//                        )
//                } else {
//                    createMonoError<Otp>(BadDataException(IOTError.IOT0105))
//                        .auditOnError(
//                            event = AuditEvent.VERIFY_OTP,
//                            metadata = mapOf("otpId" to it.otpId),
//                            userId = it.userId
//                        )
//                }
//            }
//            .logOnSuccess(message = "Successfully verified otp")
//            .logOnError(errorMessage = "Failed to verify otp")
//            .flatMap {
//                tokenService.generateToken(
//                    userId = it.userId,
//                    expiredAt = LocalDateTime.now().plusMinutes(10),
//                    otpId = it.otpId
//                )
//            }
//    }
//
//    private fun sendEmail(otp: Otp, userDetails: UserDetails): Otp {
//        mqttPublisher.publish(
//            MqttTopicName.COMMUNICATION, CommunicationMessage(
//                type = CommunicationType.OTP,
//                to = otp.userId,
//                userId = otp.userId,
//                metadata = mapOf("otp" to otp.value)
//            )
//        )
//        return otp
//    }
}
