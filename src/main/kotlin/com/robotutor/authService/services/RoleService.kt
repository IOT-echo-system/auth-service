package com.robotutor.authService.services

import com.robotutor.authService.models.*
import com.robotutor.authService.repositories.RoleRepository
import com.robotutor.iot.service.IdGeneratorService
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val idGeneratorService: IdGeneratorService,
    private val policyService: PolicyService
) {
    private val logger = Logger(this::class.java)
    fun createRole(name: String): Mono<Role> {
        return idGeneratorService.generateId(IdType.ROLE_ID).flatMap {
            roleRepository.save(Role(roleId = it, name = name))
        }
            .logOnSuccess(logger, "Successfully created role: $name")
    }

    fun addPolicies(roleId: RoleId, policies: List<PolicyId>): Mono<Role> {
        return roleRepository.findByRoleId(roleId).flatMap {
            roleRepository.save(it.addPolicies(policies))
        }
    }

    fun getPoliciesByRoleId(roleId: RoleId): Flux<Policy> {
        return roleRepository.findByRoleId(roleId)
            .flatMapMany {
                policyService.getPolicyByPolicies(it.policies)
            }
    }
}
