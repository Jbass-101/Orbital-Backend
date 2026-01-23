package com.jbass.routes

import com.jbass.domain.model.SubscriptionCommand
import com.jbass.domain.model.DeviceCommand
import com.jbass.domain.repository.DeviceRepository

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArraySet

// ---- Client session tracking ----

data class ClientSession(
    val session: DefaultWebSocketServerSession,
    val subscribedZones: MutableSet<String> = mutableSetOf()
)

private val clients = CopyOnWriteArraySet<ClientSession>()

// ---- WebSocket route ----

fun Route.deviceRoutes(repository: DeviceRepository) {
    webSocket("/device") {

        val client = ClientSession(this)
        clients += client
        val log = call.application.log

        try {
            // Send initial state
            send(Json.encodeToString(repository.getAll()))

            for (frame in incoming) {
                if (frame !is Frame.Text) continue

                val msg = frame.readText()

                try {
                    // ---- Subscription handling ----
                    if (msg.contains("subscribeZones")) {
                        val sub = Json.decodeFromString<SubscriptionCommand>(msg)
                        client.subscribedZones.addAll(sub.subscribeZones)
                        client.subscribedZones.removeAll(sub.unsubscribeZones)
                        continue
                    }

                    // ---- Device command handling ----
                    val command = Json.decodeFromString<DeviceCommand>(msg)

                    val device = repository.getById(command.deviceId) ?: continue
                    val updated = device.copy(state = command.newState)

                    if (repository.update(updated)) {
                        val payload = Json.encodeToString(updated)

                        clients.forEach { c ->
                            if (
                                command.zoneId == null ||
                                c.subscribedZones.contains(command.zoneId)
                            ) {
                                c.session.send(payload)
                            }
                        }
                    }

                } catch (e: Exception) {
                    log.error("WebSocket message processing failed", e)
                }
            }
        } finally {
            clients -= client
            log.info("WebSocket client disconnected")
        }
    }
}
