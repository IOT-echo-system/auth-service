package com.robotutor.authService.gateway

import com.robotutor.authService.config.UserServiceGatewayConfig
import com.robotutor.authService.gateway.views.UserIdResponse
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono

@Service
class UserServiceGateway(
    private val userServiceGatewayConfig: UserServiceGatewayConfig,
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig
) {
    fun getUserId(email: String): Mono<UserIdResponse> {
        val queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
        queryParams.add("email", email)
        return webClient.get(
            baseUrl = userServiceGatewayConfig.baseUrl,
            path = userServiceGatewayConfig.userIdPath,
            queryParams = queryParams,
            returnType = UserIdResponse::class.java,
            headers = mapOf(HttpHeaders.AUTHORIZATION to appConfig.internalAccessToken)
        )
    }

}
