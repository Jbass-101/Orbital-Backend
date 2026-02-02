package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.device.SmartDevice
import com.jbass.orbital.domain.model.weather.CurrentWeather
import com.jbass.orbital.domain.model.weather.WeatherCondition
import com.jbass.orbital.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class InMemoryWeatherRepository(
    private val weather: CurrentWeather,
) : WeatherRepository {

    // In InMemoryDeviceRepository.kt
    private val _weatherUpdates = MutableSharedFlow<CurrentWeather>(extraBufferCapacity = 1)

    override val weatherUpdates = _weatherUpdates.asSharedFlow()

    override suspend fun updateWeather(weather: CurrentWeather) {
        _weatherUpdates.emit(weather)
    }

    override fun getCurrentWeather(): CurrentWeather =
        weather
}