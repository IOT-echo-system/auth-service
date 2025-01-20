package com.robotutor.authService.services

import com.robotutor.authService.models.IdType
import com.robotutor.authService.models.Policy
import com.robotutor.authService.models.PolicyId
import com.robotutor.authService.repositories.PolicyRepository
import com.robotutor.iot.service.IdGeneratorService
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class PolicyService(
    private val policyRepository: PolicyRepository,
    private val idGeneratorService: IdGeneratorService
) {
    val logger = Logger(this::class.java)
    fun createPolicy(name: String): Mono<Policy> {
        return policyRepository.findByName(name)
            .switchIfEmpty {
                idGeneratorService.generateId(IdType.POLICY_ID).flatMap {
                    policyRepository.save(Policy(policyId = it, name = name))
                }
                    .logOnSuccess(logger, "Successfully created policy: $name")
            }
    }

    fun getPolicyByPolicies(policies: List<PolicyId>): Flux<Policy> {
        return policyRepository.findAllByPolicyIdIn(policies)
    }
}
