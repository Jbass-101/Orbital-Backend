package com.jbass.orbital.domain.model.device

import com.jbass.orbital.domain.model.device.DeviceMetadata
import com.jbass.orbital.domain.model.device.DeviceType
import kotlinx.serialization.Serializable

/**
 * This is the Basic Model of a Smart Device
 */
@Serializable
data class SmartDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val state: DeviceState,
    val zoneId: String,
    val metadata: DeviceMetadata
)