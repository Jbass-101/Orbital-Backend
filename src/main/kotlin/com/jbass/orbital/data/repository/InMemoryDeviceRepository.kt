package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.device.SmartDevice
import com.jbass.orbital.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.ConcurrentHashMap

class InMemoryDeviceRepository(
    initialDevices: List<SmartDevice>
) : DeviceRepository {

    private val devices = ConcurrentHashMap<String, SmartDevice>()

    // In InMemoryDeviceRepository.kt
    private val _deviceUpdate = MutableSharedFlow<SmartDevice>(extraBufferCapacity = 64)

    override val deviceUpdates = _deviceUpdate.asSharedFlow()

    init {
        initialDevices.forEach { devices[it.id] = it }
    }

    override fun getAllDevices(): List<SmartDevice> =
        devices.values.toList()

    override fun getDeviceById(id: String): SmartDevice? =
        devices[id]

    override fun updateDevice(device: SmartDevice): Boolean {
        devices.replace(device.id, device) != null
        _deviceUpdate.tryEmit(device)
        return true
    }



}
