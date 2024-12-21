package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val ROLE_COLLECTION = "roles"

@TypeAlias("Role")
@Document(ROLE_COLLECTION)
data class Role(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val roleId: RoleId,
    val name: String,
    val policies: MutableList<PolicyId> = mutableListOf(),
) {
    fun addPolicies(policies: List<PolicyId>): Role {
        this.policies.plus(policies)
        return this
    }
}

typealias RoleId = String
