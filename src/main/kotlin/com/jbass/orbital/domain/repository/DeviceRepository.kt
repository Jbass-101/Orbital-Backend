package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.device.SmartDevice
import kotlinx.coroutines.flow.SharedFlow

/**Interface so we can attach to Remote or local database*/
interface DeviceRepository {
    val deviceUpdates: SharedFlow<SmartDevice>
    fun getAllDevices(): List<SmartDevice>
    fun getDeviceById(id: String): SmartDevice?
    fun updateDevice(device: SmartDevice): Boolean
}
