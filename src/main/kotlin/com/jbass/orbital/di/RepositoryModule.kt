package com.jbass.orbital.di

import com.jbass.orbital.data.mock.MockDeviceDataProvider
import com.jbass.orbital.data.mock.randomCurrentWeather
import com.jbass.orbital.data.repository.InMemoryDeviceRepository
import com.jbass.orbital.data.repository.InMemoryWeatherRepository
import com.jbass.orbital.data.repository.InMemoryZoneRepository
import com.jbass.orbital.domain.repository.DeviceRepository
import com.jbass.orbital.domain.repository.WeatherRepository
import com.jbass.orbital.domain.repository.ZoneRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<DeviceRepository>(
        named("mock")
    ) {
        InMemoryDeviceRepository(
            initialDevices = MockDeviceDataProvider.devices
        )
    }

    single<WeatherRepository>(
        named("mock")
    ) {
        InMemoryWeatherRepository(
            weather = randomCurrentWeather(),
        )
    }

    single<ZoneRepository>(
        named("mock")
    ) {
        InMemoryZoneRepository(
            initialZones = MockDeviceDataProvider.zones
        )
    }
}