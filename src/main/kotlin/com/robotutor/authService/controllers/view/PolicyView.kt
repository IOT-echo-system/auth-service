package com.robotutor.authService.controllers.view

import com.robotutor.authService.models.Policy
import com.robotutor.authService.models.PolicyId

data class PolicyView(
    val policyId: PolicyId,
    val name: String,
) {
    companion object {
        fun from(policy: Policy): PolicyView {
            return PolicyView(policyId = policy.policyId, name = policy.name)
        }
    }
}
