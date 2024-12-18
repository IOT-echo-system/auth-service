package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val POLICY_COLLECTION = "policies"

@TypeAlias("Role")
@Document(POLICY_COLLECTION)
data class Policy(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val policyId: PolicyId,
    val policyName: PolicyName,
)

typealias PolicyId = String

enum class PolicyName {

}
