package com.jbass.data.repository

import com.jbass.domain.model.SmartDevice
import com.jbass.domain.repository.DeviceRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryDeviceRepository(
    initialDevices: List<SmartDevice>
) : DeviceRepository {

    private val devices = ConcurrentHashMap<String, SmartDevice>()

    init {
        initialDevices.forEach { devices[it.id] = it }
    }

    override fun getAll(): List<SmartDevice> =
        devices.values.toList()

    override fun getById(id: String): SmartDevice? =
        devices[id]

    override fun update(device: SmartDevice): Boolean =
        devices.replace(device.id, device) != null
}
