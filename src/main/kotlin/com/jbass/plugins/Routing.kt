package com.jbass.plugins

import com.jbass.data.mock.MockDataProvider
import com.jbass.data.repository.InMemoryDeviceRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.jbass.routes.deviceRoutes

fun Application.configureRouting() {

    val repository = InMemoryDeviceRepository(
        initialDevices = MockDataProvider.devices
    )

    routing {
        deviceRoutes(repository)
    }
}
