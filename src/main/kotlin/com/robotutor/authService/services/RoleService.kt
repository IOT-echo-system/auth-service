package com.robotutor.authService.services

import com.robotutor.authService.models.IdType
import com.robotutor.authService.models.Policy
import com.robotutor.authService.models.Role
import com.robotutor.authService.models.RoleId
import com.robotutor.authService.repositories.RoleRepository
import com.robotutor.iot.service.IdGeneratorService
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
    fun createRole(name: String): Mono<Role> {
        return idGeneratorService.generateId(IdType.POLICY_ID).flatMap {
            roleRepository.save(Role(roleId = it, name = name))
        }
            .logOnSuccess("Successfully created role: $name")
    }

    fun getPoliciesByRoleId(roleId: RoleId): Flux<Policy> {
        return roleRepository.findByRoleId(roleId)
            .flatMapMany {
                policyService.getPolicyByPolicies(it.policies)
            }
    }
}
