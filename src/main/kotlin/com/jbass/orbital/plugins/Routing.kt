package com.jbass.orbital.plugins

import com.jbass.orbital.data.mock.MockDeviceDataProvider
import com.jbass.orbital.data.mock.randomCurrentWeather
import com.jbass.orbital.data.repository.InMemoryDeviceRepository
import com.jbass.orbital.data.repository.InMemoryWeatherRepository
import com.jbass.orbital.data.repository.InMemoryZoneRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.jbass.orbital.routes.deviceRoutes

fun Application.configureRouting() {
    routing {
        deviceRoutes()
    }
}
