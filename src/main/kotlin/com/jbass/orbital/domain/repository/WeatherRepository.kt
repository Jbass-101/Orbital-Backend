package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.weather.CurrentWeather
import kotlinx.coroutines.flow.SharedFlow

interface WeatherRepository {
    val weatherUpdates: SharedFlow<CurrentWeather>
    suspend fun updateWeather(weather: CurrentWeather)
    fun getCurrentWeather(): CurrentWeather
}

