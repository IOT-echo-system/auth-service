package com.robotutor.authService.repositories

import com.robotutor.authService.models.Policy
import com.robotutor.authService.models.PolicyId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PolicyRepository : ReactiveCrudRepository<Policy, PolicyId> {
    fun findByName(name: String): Mono<Policy>
    fun findAllByPolicyIdIn(ids: List<PolicyId>): Flux<Policy>
    fun findAllByNameIn(names: List<String>): Flux<Policy>
}
