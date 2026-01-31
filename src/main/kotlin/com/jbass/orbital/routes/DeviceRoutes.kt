package com.jbass.orbital.routes

import com.jbass.orbital.domain.model.ClientMessage
import com.jbass.orbital.domain.model.ErrorCode
import com.jbass.orbital.domain.model.ServerMessage
import com.jbass.orbital.domain.repository.DeviceRepository
import com.jbass.orbital.domain.repository.WeatherRepository
import com.jbass.orbital.domain.repository.ZoneRepository

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.send
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArraySet

// ---- Client session tracking ----

/**
 * Wraps the raw WebSocket connection (DefaultWebSocketServerSession)
 * with application-specific metadata (subscribedZones).
 * * This allows us to implement "Room Filtering" — sending updates
 * only to users currently looking at a specific room.
 */
data class ClientSession(
    val session: DefaultWebSocketServerSession,
    // Using a MutableSet here allows us to dynamically change what this user "sees"
    val subscribedZones: MutableSet<String> = mutableSetOf()
)

/**
 * A thread-safe collection of all currently connected users.
 * * Why CopyOnWriteArraySet?
 * It is optimized for scenarios where we read (iterate/broadcast) frequently
 * but write (connect/disconnect) rarely. It prevents ConcurrentModificationException
 * during the broadcast loop.
 */
private val clients = CopyOnWriteArraySet<ClientSession>()

private val webSocketJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    classDiscriminator = "classType"
}

// ---- WebSocket route ----

fun Route.deviceRoutes(weatherRepository: WeatherRepository,
                       deviceRepository: DeviceRepository,
                       zoneRepository: ZoneRepository) {
    /**
     * Defines the endpoint: ws://[server-ip]:[port]/device
     * The app connects here to start the real-time stream.
     */
    webSocket("/orbital/device") {

        // Connection Lifecycle: Handshake complete.
        // Wrap this new connection and add it to our global tracking list.
        val client = ClientSession(this)
        clients += client

        val log = call.application.log

        try {
            // Initial Sync
            // Immediately send the full current state of the house.
            // This ensures the UI is never empty while waiting for the first update.
            // We wrap this in a StateUpdate message so the client parser remains consistent
            val initialPayload = ServerMessage.FullStateUpdate(
                weatherRepository.getWeather(),
                deviceRepository.getAll(),
                zoneRepository.getAllZones())
            send(webSocketJson.encodeToString<ServerMessage>(initialPayload))

            val _x = Json.encodeToString(initialPayload)
            val sizeInBytes = _x.toByteArray(Charsets.UTF_8).size


            log.info("Initial payload size: $sizeInBytes bytes")
            log.info("Initial payload : \n $initialPayload")

            // The Listen Loop:
            // This loop keeps the connection open and waits for incoming messages.
            for (frame in incoming) {
                if (frame !is Frame.Text) continue

                log.info("Client frame size: ${frame.data.size} bytes")

                try {
                    //Message from client
                    val msg = frame.readText()

                    log.info(msg)

                    //Decode Message
                    //now we handle the possible cases of Client Message
                    when (val message = webSocketJson.decodeFromString<ClientMessage>(msg)) {
                        is ClientMessage.Subscribe -> {
                            client.subscribedZones.addAll(message.subscribeZones)
                            client.subscribedZones.removeAll(message.unsubscribeZones)

                            // Send Success ACK back to this specific client
                            val ack = ServerMessage.CommandAck(
                                requestId = message.requestId,
                                success = true
                            )
                            send(webSocketJson.encodeToString<ServerMessage>(ack))

                        }
                        is ClientMessage.Command -> {
                            val device = deviceRepository.getById(message.deviceId)

                            if (device == null) {
                                // CASE: Device Not Found - Send Failure ACK
                                val errorAck = ServerMessage.CommandAck(
                                    requestId = message.requestId,
                                    success = false,
                                    errorCode = ErrorCode.DEVICE_NOT_FOUND,
                                    message = "Device ID ${message.deviceId} does not exist."
                                )
                                send(webSocketJson.encodeToString<ServerMessage>(errorAck))

                            }else {
                                // CASE: Valid Device - We Copy the existing device and update it with the new state
                                val updatedDevice = device.copy(state = message.newState)

                                if (deviceRepository.update(updatedDevice)) {
                                    // 1. Send Success ACK to the SENDER
                                    val successAck = ServerMessage.CommandAck(
                                        requestId = message.requestId,
                                        success = true
                                    )
                                    send(webSocketJson.encodeToString<ServerMessage>(successAck))



                                    // Broadcast State Change to EVERYONE (including sender)
                                    //Ensures source of truth
                                    val broadcastMsg = ServerMessage.DeltaStateUpdate(listOf(updatedDevice))
                                    val broadcastJson = webSocketJson.encodeToString<ServerMessage>(broadcastMsg)

                                    clients.forEach { c ->
                                        if (message.zoneId == null || c.subscribedZones.contains(message.zoneId)) {
                                            c.session.send(broadcastJson)
                                        }
                                    }
                                }else {
                                    // CASE: DB Update Failed
                                    send(webSocketJson.encodeToString<ServerMessage>(ServerMessage.CommandAck(
                                        requestId = message.requestId,
                                        success = false,
                                        errorCode = ErrorCode.SERVER_ERROR
                                    )))
                                }

                            }

                        }
                    }

                } catch (e: SerializationException) {
                    // Handle JSON parsing errors
                    val errorAck = ServerMessage.CommandAck(
                        requestId = "",
                        success = false,
                        errorCode = ErrorCode.INVALID_COMMAND,
                        message = "Invalid JSON format: ${e.message}"
                    )
                    send(webSocketJson.encodeToString<ServerMessage>(errorAck))
                    log.error("JSON parsing failed", e)
                }catch (e: Exception) {
                    // CASE: Device Not Found - Send Failure ACK
                    val errorAck = ServerMessage.CommandAck(
                        requestId = "",
                        success = false,
                        errorCode = ErrorCode.INVALID_COMMAND,
                        message = "Invalid Command Received."
                    )
                    send(webSocketJson.encodeToString<ServerMessage>(errorAck))

                    // 8. Error Isolation:
                    // If one bad message arrives, log it, but DO NOT crash the
                    // loop. Keep the connection alive for future valid messages.
                    log.error("WebSocket message processing failed", e)
                }
            }
        } finally {
            // 9. Cleanup:
            // When the loop ends (socket closed, network lost, or error),
            // remove the client to prevent memory leaks and "dead" broadcasts.
            clients -= client
            log.info("WebSocket client disconnected")
        }
    }
}