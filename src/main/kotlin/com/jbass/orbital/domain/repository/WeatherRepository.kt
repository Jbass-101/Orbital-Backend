package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.SmartDevice
import com.jbass.orbital.domain.model.weather.CurrentWeather

interface WeatherRepository {
    fun getWeather(): CurrentWeather
}

