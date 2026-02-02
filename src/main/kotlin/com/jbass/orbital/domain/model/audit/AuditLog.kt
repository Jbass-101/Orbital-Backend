package com.jbass.orbital.domain.model.audit

import kotlinx.serialization.Serializable

@Serializable
data class AuditLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val deviceId: String,
    val userId: String,
    val command: String,
    val status: String,
    val origin: String
)