package com.jbass.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * Configures the JSON engine to handle polymorphic serialization.
 * This allows the server to correctly identify different DeviceState
 * types using a 'type' discriminator field in the JSON.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            // Ensures the sealed class discriminator is included in the JSON output
            encodeDefaults = true
        })
    }
}