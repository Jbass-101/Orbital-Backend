package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.SmartDevice

/**Interface so we can attach to Remote or local database*/
interface DeviceRepository {
    fun getAllDevices(): List<SmartDevice>
    fun getDeviceById(id: String): SmartDevice?
    fun updateDevice(device: SmartDevice): Boolean
}
