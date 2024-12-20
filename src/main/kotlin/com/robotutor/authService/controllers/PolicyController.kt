package com.robotutor.authService.controllers

import com.robotutor.authService.controllers.view.PolicyView
import com.robotutor.authService.services.RoleService
import com.robotutor.iot.utils.models.UserData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/policies")
class PolicyController(
    private val roleService: RoleService
) {

    @GetMapping
    fun validateToken(userData: UserData): Flux<PolicyView> {
        return roleService.getPoliciesByRoleId(userData.roleId).map { PolicyView.from(it) }
    }
}
