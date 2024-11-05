package com.robotutor.authService.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.user-service")
data class UserServiceGatewayConfig(val baseUrl: String, val userIdPath: String)
