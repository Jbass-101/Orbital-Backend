package com.jbass.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceCommand(
    val deviceId: String,
    val newState: DeviceState,
    val zoneId: String? = null  // optional: if client wants zone filtering
)
