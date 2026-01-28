package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.weather.CurrentWeather
import com.jbass.orbital.domain.repository.WeatherRepository


class InMemoryWeatherRepository(
    private val weather: CurrentWeather,
) : WeatherRepository {

    override fun getWeather(): CurrentWeather =
        weather
}