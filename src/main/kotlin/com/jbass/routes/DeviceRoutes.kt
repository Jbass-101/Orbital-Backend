package com.jbass.routes

import com.jbass.domain.model.DeviceCommand
import com.jbass.domain.repository.DeviceRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe set of all active WebSocket sessions
 */
private val sessions = ConcurrentHashMap.newKeySet<DefaultWebSocketServerSession>()

fun Route.deviceRoutes(repository: DeviceRepository) {
    webSocket("/device") {
        sessions += this
        val log = this.call.application.log

        try {
            // Send initial state
            val initialState = Json.encodeToString(repository.getAll())
            send(initialState)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    try {
                        // Deserialize the command instead of full device
                        val command = Json.decodeFromString<DeviceCommand>(frame.readText())

                        // Update the device
                        val device = repository.getById(command.deviceId)
                        if (device != null) {
                            val updatedDevice = device.copy(state = command.newState)
                            if (repository.update(updatedDevice)) {
                                // Broadcast only the updated device
                                val updatedJson = Json.encodeToString(updatedDevice)
                                sessions.forEach { it.send(updatedJson) }
                            }
                        }
                    } catch (e: Exception) {
                        log.error("Failed to process WebSocket command", e)
                    }
                }
            }
        } finally {
            sessions -= this
            log.info("WebSocket session closed: $this")
        }
    }
}
