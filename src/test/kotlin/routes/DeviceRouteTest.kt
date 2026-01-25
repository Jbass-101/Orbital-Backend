package com.jbass.routes

import com.jbass.data.mock.MockDataProvider
import com.jbass.data.repository.InMemoryDeviceRepository
import com.jbass.domain.model.*
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
import io.ktor.server.config.MapApplicationConfig
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
}