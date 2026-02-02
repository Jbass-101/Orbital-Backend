package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.audit.AuditLog


interface AuditRepository {
    suspend fun log(entry: AuditLog)
    suspend fun getLogsForDevice(deviceId: String): List<AuditLog>
    suspend fun getRecentLogs(limit: Int = 50): List<AuditLog>
}