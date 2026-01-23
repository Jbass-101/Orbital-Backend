package domain.repository

import domain.model.SmartDevice

/**Interface so we can attach to Remote or local database*/
interface DeviceRepository {
    fun getAll(): List<SmartDevice>
    fun getById(id: String): SmartDevice?
    fun update(device: SmartDevice): Boolean
}
