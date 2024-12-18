package com.robotutor.authService.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneId

const val DEVICE_COLLECTION = "devices"

@TypeAlias("Device")
@Document(DEVICE_COLLECTION)
data class DeviceDetails(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: DeviceId,
    var password: String,
    val registeredAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),
)
typealias DeviceId = String
