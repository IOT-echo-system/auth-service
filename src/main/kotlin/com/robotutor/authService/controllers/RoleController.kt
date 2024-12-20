package com.robotutor.authService.controllers

import com.robotutor.authService.controllers.view.PolicyView
import com.robotutor.authService.models.RoleId
import com.robotutor.authService.services.RoleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService
) {

    @GetMapping("/{roleId}/policies")
    fun validateToken(@PathVariable roleId: RoleId): Flux<PolicyView> {
        return roleService.getPoliciesByRoleId(roleId).map { PolicyView.from(it) }
    }
}
