package com.jbass.orbital.data.mock

import com.jbass.orbital.domain.model.weather.CurrentWeather
import com.jbass.orbital.domain.model.weather.WeatherCondition
import com.jbass.orbital.domain.model.weather.WeatherLocation
import kotlin.random.Random

fun randomCurrentWeather(): CurrentWeather {
    val random = Random.Default

    val possibleWeathers = List(100) {
        val location = locations.random()

        CurrentWeather(
            temperature = random.nextDouble(-5.0, 38.0),   // realistic global range
            humidity = random.nextInt(20, 95),              // %
            pressure = random.nextInt(980, 1050),           // hPa
            windSpeed = random.nextDouble(0.0, 20.0),       // m/s
            visibility = random.nextInt(1_000, 10_000),     // meters
            condition = WeatherCondition.entries.random(),
            location = location
        )
    }

    return possibleWeathers.random()
}

private val locations = listOf(
    WeatherLocation("Cape Town", "South Africa"),
    WeatherLocation("New York", "United States"),
    WeatherLocation("London", "United Kingdom"),
    WeatherLocation("Tokyo", "Japan"),
    WeatherLocation("Paris", "France"),
    WeatherLocation("Sydney", "Australia"),
    WeatherLocation("Berlin", "Germany"),
    WeatherLocation("Toronto", "Canada"),
    WeatherLocation("Dubai", "United Arab Emirates"),
    WeatherLocation("São Paulo", "Brazil")
)
