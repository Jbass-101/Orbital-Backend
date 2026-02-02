package com.jbass.orbital.di

import com.jbass.orbital.data.mock.MockDeviceDataProvider
import com.jbass.orbital.data.mock.randomCurrentWeather
import com.jbass.orbital.data.repository.InMemoryAuditRepository
import com.jbass.orbital.data.repository.InMemoryDeviceRepository
import com.jbass.orbital.data.repository.InMemoryWeatherRepository
import com.jbass.orbital.data.repository.InMemoryZoneRepository
import com.jbass.orbital.domain.repository.AuditRepository
import com.jbass.orbital.domain.repository.DeviceRepository
import com.jbass.orbital.domain.repository.WeatherRepository
import com.jbass.orbital.domain.repository.ZoneRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<DeviceRepository>(
    ) {
        InMemoryDeviceRepository(
            initialDevices = MockDeviceDataProvider.devices
        )
    }

    single<WeatherRepository>(
    ) {
        InMemoryWeatherRepository(
            weather = randomCurrentWeather(),
        )
    }

    single<ZoneRepository>(
    ) {
        InMemoryZoneRepository(
            initialZones = MockDeviceDataProvider.zones
        )
    }
    single<AuditRepository> { InMemoryAuditRepository() }

}