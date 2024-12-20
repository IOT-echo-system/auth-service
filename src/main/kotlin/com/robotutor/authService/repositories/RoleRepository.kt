package com.robotutor.authService.repositories

import com.robotutor.authService.models.Role
import com.robotutor.authService.models.RoleId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RoleRepository : ReactiveCrudRepository<Role, RoleId> {
    fun findByRoleId(roleId: String): Mono<Role>
}
