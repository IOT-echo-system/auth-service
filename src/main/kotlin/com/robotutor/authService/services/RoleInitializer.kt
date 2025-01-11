package com.robotutor.authService.services

import com.robotutor.authService.repositories.PolicyRepository
import com.robotutor.authService.repositories.RoleRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
@Order(2)
class RoleInitializer(
    private val roleService: RoleService,
    private val policyRepository: PolicyRepository,
    private val roleRepository: RoleRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val roleWithPolicies = listOf(
            mapOf(
                "OWNER" to listOf(
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
                    "FEED:DELETE",
                    "ROUTINE:CREATE",
                    "ROUTINE:READ",
                    "ROUTINE:UPDATE",
                    "ROUTINE:DELETE",
                )
            ),
            mapOf(
                "ADMIN" to listOf(
                    "PREMISES:CREATE",
                    "PREMISES:READ",
                    "PREMISES:UPDATE",
                    "ZONE:CREATE",
                    "ZONE:READ",
                    "ZONE:UPDATE",
                    "BOARD:CREATE",
                    "BOARD:READ",
                    "BOARD:UPDATE",
                    "FEED:CREATE",
                    "FEED:READ",
                    "FEED:UPDATE",
                    "ROUTINE:CREATE",
                    "ROUTINE:READ",
                    "ROUTINE:UPDATE",
                )
            ),
            mapOf(
                "USER" to listOf(
                    "PREMISES:READ",
                    "ZONE:READ",
                    "BOARD:READ",
                    "FEED:READ",
                    "ROUTINE:READ",
                )
            ),
        )
        roleRepository.findAll()
            .switchIfEmpty(
                Flux.fromIterable(roleWithPolicies)
                    .flatMapSequential { it: Map<String, List<String>> ->
                        val name = it.keys.first()
                        val policies = it.getOrDefault(name, emptyList())
                        roleService.createRole(name)
                            .flatMap { role ->
                                policyRepository.findAllByNameIn(policies)
                                    .map { it.policyId }
                                    .collectList()
                                    .flatMap {
                                        roleService.addPolicies(role.roleId, it)
                                    }
                            }
                    }
            )
            .then()
            .block()
    }
}
