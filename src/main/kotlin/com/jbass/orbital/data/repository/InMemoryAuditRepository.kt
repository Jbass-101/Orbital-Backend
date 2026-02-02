package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.audit.AuditLog
import com.jbass.orbital.domain.repository.AuditRepository
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryAuditRepository : AuditRepository {

    // CopyOnWriteArrayList is thread-safe for reading/writing from multiple coroutines
    private val logs = CopyOnWriteArrayList<AuditLog>()

    override suspend fun log(entry: AuditLog) {
        // We add to the beginning so recent logs are at index 0
        logs.add(0, entry)

        // Optional: Keep only the last 1000 logs to prevent memory leaks
        if (logs.size > 1000) {
            logs.removeAt(logs.size - 1)
        }
    }

    override suspend fun getLogsForDevice(deviceId: String): List<AuditLog> {
        return logs.filter { it.deviceId == deviceId }
    }

    override suspend fun getRecentLogs(limit: Int): List<AuditLog> {
        return logs.take(limit)
    }
}