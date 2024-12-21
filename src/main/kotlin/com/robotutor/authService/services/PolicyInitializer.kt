package com.robotutor.authService.services

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
@Order(1)
class PolicyInitializer(private val policyService: PolicyService) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val policies = listOf(
            "PREMISES:CREATE",
            "PREMISES:READ",
            "PREMISES:UPDATE",
            "PREMISES:DELETE",
            "ZONE:CREATE",
            "ZONE:READ",
            "ZONE:UPDATE",
            "ZONE:DELETE",
            "DEVICE:CREATE",
            "DEVICE:READ",
            "DEVICE:UPDATE",
            "DEVICE:DELETE",
            "ROUTINE:CREATE",
            "ROUTINE:READ",
            "ROUTINE:UPDATE",
            "ROUTINE:DELETE",
        )
        Flux.fromIterable(policies)
            .flatMapSequential {
                policyService.createPolicy(it)
            }
            .subscribe()
    }
}
