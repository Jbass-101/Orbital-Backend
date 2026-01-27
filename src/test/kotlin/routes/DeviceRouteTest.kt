package com.jbass.routes

import com.jbass.orbital.data.mock.MockDataProvider
import com.jbass.orbital.data.repository.InMemoryDeviceRepository
import com.jbass.orbital.domain.model.ClientMessage
import com.jbass.orbital.domain.model.DeviceState
import com.jbass.orbital.domain.model.ErrorCode
import com.jbass.orbital.domain.model.ServerMessage
import com.jbass.orbital.routes.deviceRoutes
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// EXPLICIT IMPORTS
import io.ktor.server.application.install
import io.ktor.server.websocket.WebSockets
import io.ktor.server.routing.routing
import io.ktor.client.plugins.websocket.WebSockets as ClientWebSockets

class DeviceRoutesTest {

    // Define a shared JSON config to ensure client and server speak the same language
    private val jsonConfig = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "classType"
    }

    @Test
    fun `test client receives ACK after command with debug`() = testApplication {
        // 1. SETUP SERVER
        val mockRepo = InMemoryDeviceRepository(MockDataProvider.devices)

        application {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
            routing {
                deviceRoutes(mockRepo)
            }
        }

        // 2. SETUP CLIENT
        val client = createClient {
            install(ClientWebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
        }

        // 3. RUN TEST with try-catch for debugging
        try {
            client.webSocket("/device") {
                println("DEBUG: WebSocket connected")

                try {
                    // A. Consume Initial State
                    val initialFrame = incoming.receive()
                    if (initialFrame is Frame.Text) {
                        println("DEBUG: Received initial state: ${initialFrame.readText()}")
                    } else {
                        println("DEBUG: Received non-text initial frame: $initialFrame")
                    }

                    // B. Send Command
                    val badCommand = ClientMessage.Command(
                        requestId = "test-req-123",
                        deviceId = "invalid-id",
                        newState = DeviceState.OnOff(true)
                    )

                    val commandJson = jsonConfig.encodeToString<ClientMessage>(badCommand)
                    println("DEBUG: Sending command: $commandJson")
                    send(commandJson)

                    // C. Wait for response
                    println("DEBUG: Waiting for response...")
                    val responseFrame = incoming.receive()

                    if (responseFrame is Frame.Text) {
                        val responseText = responseFrame.readText()
                        println("DEBUG: Received response: $responseText")

                        val response = jsonConfig.decodeFromString<ServerMessage>(responseText)
                        println("DEBUG: Parsed response: $response")

                        assertTrue(response is ServerMessage.CommandAck)
                        assertEquals("test-req-123", response.requestId)
                        assertEquals(ErrorCode.DEVICE_NOT_FOUND, response.errorCode)
                    } else {
                        println("ERROR: Expected text frame, got: $responseFrame")
                        throw AssertionError("Expected text frame for response")
                    }

                } catch (e: Exception) {
                    println("ERROR in WebSocket communication: ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }
        } catch (e: Exception) {
            println("ERROR establishing WebSocket connection: ${e.message}")
            e.printStackTrace()
            throw e
        }

        println("DEBUG: Test completed successfully")
    }

    @Test
    fun `test valid command updates device and broadcasts state`() = testApplication {
        // 1. Setup with a known device
        val mockRepo = InMemoryDeviceRepository(MockDataProvider.devices)
        val targetDevice = MockDataProvider.devices.first() // Pick the first existing device

        application {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
            routing { deviceRoutes(mockRepo) }
        }

        val client = createClient {
            install(ClientWebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
        }

        client.webSocket("/device") {
            // A. Ignore Initial State
            incoming.receive()

            // B. Send Valid Command (Toggle Light ON)
            val validCommand = ClientMessage.Command(
                requestId = "req-happy-path",
                deviceId = targetDevice.id,
                newState = DeviceState.OnOff(true)
            )
            send(Frame.Text(jsonConfig.encodeToString<ClientMessage>(validCommand)))

            // C. Verify Success ACK
            val ackFrame = incoming.receive() as Frame.Text
            val ackMsg = jsonConfig.decodeFromString<ServerMessage>(ackFrame.readText())

            assertTrue(ackMsg is ServerMessage.CommandAck)
            assertTrue(ackMsg.success, "Command should succeed")
            assertEquals("req-happy-path", ackMsg.requestId)

            // D. Verify Broadcast (The confirmation that the light actually turned ON)
            val broadcastFrame = incoming.receive() as Frame.Text
            val broadcastMsg = jsonConfig.decodeFromString<ServerMessage>(broadcastFrame.readText())

            assertTrue(broadcastMsg is ServerMessage.FullStateUpdate)
            val updatedDevice = broadcastMsg.devices.find { it.id == targetDevice.id }
            assertTrue(updatedDevice!!.state is DeviceState.OnOff)
            assertTrue((updatedDevice.state as DeviceState.OnOff).isOn)
        }
    }

    @Test
    fun `test server handles malformed JSON gracefully`() = testApplication {
        val mockRepo = InMemoryDeviceRepository(emptyList())

        application {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
            routing { deviceRoutes(mockRepo) }
        }

        createClient {
            install(ClientWebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
        }.webSocket("/device") {
            incoming.receive() // Ignore initial state

            // Send Garbage Data
            send(Frame.Text("{ \"bad\": \"json\" }"))

            // Expect Error ACK
            val responseFrame = incoming.receive() as Frame.Text
            val response = jsonConfig.decodeFromString<ServerMessage>(responseFrame.readText())

            assertTrue(response is ServerMessage.CommandAck)
            assertEquals(false, response.success)
            assertEquals(ErrorCode.INVALID_COMMAND, response.errorCode)
        }
    }

    @Test
    fun `test subscription command returns success ACK`() = testApplication {
        val mockRepo = InMemoryDeviceRepository(emptyList())

        application {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
            routing { deviceRoutes(mockRepo) }
        }

        createClient {
            install(ClientWebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConfig)
            }
        }.webSocket("/device") {
            incoming.receive() // Ignore initial state

            // Send Subscribe Command
            val subCommand = ClientMessage.Subscribe(
                requestId = "sub-req-01",
                subscribeZones = setOf("zone-1", "zone-2")
            )
            send(Frame.Text(jsonConfig.encodeToString<ClientMessage>(subCommand)))

            // Expect Success ACK
            val responseFrame = incoming.receive() as Frame.Text
            val response = jsonConfig.decodeFromString<ServerMessage>(responseFrame.readText())

            assertTrue(response is ServerMessage.CommandAck)
            assertEquals("sub-req-01", response.requestId)
            assertTrue(response.success)
        }
    }
}