package com.robotutor.authService.services

import com.robotutor.authService.models.Policy
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class PolicyInitializer(private val policyService: PolicyService) {
    fun initialize(): Flux<Policy> {
        val policies = listOf(
            "PREMISES:CREATE",
            "PREMISES:READ",
            "PREMISES:UPDATE",
            "PREMISES:DELETE",
            "ZONE:CREATE",
            "ZONE:READ",
            "ZONE:UPDATE",
            "ZONE:DELETE",
            "BOARD:CREATE",
            "BOARD:READ",
            "BOARD:UPDATE",
            "BOARD:DELETE",
            "FEED:CREATE",
            "FEED:READ",
            "FEED:UPDATE",
            "FEED:UPDATE_VALUE",
            "FEED:DELETE",
            "ROUTINE:CREATE",
            "ROUTINE:READ",
            "ROUTINE:UPDATE",
            "ROUTINE:DELETE",
            "WIDGET:CREATE",
            "WIDGET:READ",
            "WIDGET:UPDATE",
            "WIDGET:DELETE",
        )
        return Flux.fromIterable(policies)
            .flatMapSequential {
                policyService.createPolicy(it)
            }
    }
}
