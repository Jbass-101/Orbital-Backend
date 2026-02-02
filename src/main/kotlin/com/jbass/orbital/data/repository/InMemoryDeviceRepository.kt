package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.SmartDevice
import com.jbass.orbital.domain.repository.DeviceRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryDeviceRepository(
    initialDevices: List<SmartDevice>
) : DeviceRepository {

    private val devices = ConcurrentHashMap<String, SmartDevice>()

    init {
        initialDevices.forEach { devices[it.id] = it }
    }

    override fun getAllDevices(): List<SmartDevice> =
        devices.values.toList()

    override fun getDeviceById(id: String): SmartDevice? =
        devices[id]

    override fun updateDevice(device: SmartDevice): Boolean =
        devices.replace(device.id, device) != null
}
