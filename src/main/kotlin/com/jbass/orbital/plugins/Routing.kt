package com.jbass.orbital.plugins

import com.jbass.orbital.data.mock.MockDataProvider
import com.jbass.orbital.data.repository.InMemoryDeviceRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.jbass.orbital.routes.deviceRoutes

fun Application.configureRouting() {

    val repository = InMemoryDeviceRepository(
        initialDevices = MockDataProvider.devices
    )

    routing {
        deviceRoutes(repository)
    }
}
