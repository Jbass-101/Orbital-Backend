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

    val deviceRepository = InMemoryDeviceRepository(
        initialDevices = MockDeviceDataProvider.devices
    )
    val weatherRepository = InMemoryWeatherRepository(
        weather = randomCurrentWeather(),
    )
    val zoneRepository = InMemoryZoneRepository(
        initialZones = MockDeviceDataProvider.zones
    )

    routing {
        deviceRoutes(
            weatherRepository,
            deviceRepository,
            zoneRepository)
    }
}
