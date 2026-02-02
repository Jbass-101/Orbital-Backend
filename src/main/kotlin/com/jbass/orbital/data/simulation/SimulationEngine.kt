package com.jbass.orbital.data.simulation


import com.jbass.orbital.domain.model.DeviceState
import com.jbass.orbital.domain.model.weather.WeatherCondition
import com.jbass.orbital.domain.repository.DeviceRepository
import com.jbass.orbital.domain.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlin.random.Random

class SimulationEngine(
    private val deviceRepository: DeviceRepository,
    private val weatherRepository: WeatherRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true

//        scope.launch {
//            while (isRunning) {
//                simulateStep()
//                delay(5000) // Run a simulation tick every 5 seconds
//            }
//        }

        scope.launch {
            while (isRunning) {
                simulateWeather()
                delay(10000) // Run a simulation tick every 10 seconds
            }
        }
    }

    private suspend fun simulateStep() {
        val devices = deviceRepository.getAllDevices()

        devices.forEach { device ->
            val newState = when (val currentState = device.state) {
                // 1. Simulate Temperature Drift
                is DeviceState.Temperature -> {
                    val drift = (Random.nextFloat() - 0.5f) * 0.1f
                    currentState.copy(current = currentState.current + drift)
                }

                // 2. Simulate Network Jitter
                is DeviceState.Network -> {
                    val latencyChange = Random.nextInt(-5, 6)
                    currentState.copy(latencyMs = (currentState.latencyMs ?: 20) + latencyChange)
                }

                // 3. Chance for a device to go Offline
                else -> if (Random.nextInt(1000) < 5) DeviceState.Offline else currentState
            }

            // 4. Update Metadata (Heartbeat)
            val newMetadata = device.metadata.copy(
                lastSeenEpochMs = System.currentTimeMillis(),
                isReachable = newState !is DeviceState.Offline
            )

            // Update the repository
            deviceRepository.updateDevice(device.copy(state = newState, metadata = newMetadata))
        }
    }

    private suspend fun simulateWeather() {
        val current = weatherRepository.getCurrentWeather()

        // 1. Simulate Temperature Drift (Fluctuates by ±0.5 degrees)
        val tempChange = (Random.nextFloat() - 0.5f) * 1.0f
        val newTemp = current.temperature + tempChange

        // 2. Simulate Condition Change (Small chance to change weather type)
        val shouldChangeCondition = Random.nextInt(100) < 5 // 5% chance per tick
        val newCondition = if (shouldChangeCondition) {
            WeatherCondition.entries.toTypedArray().random()
        } else {
            current.condition // Use your existing WeatherCondition enum
        }

        val updatedWeather = current.copy(
            temperature = newTemp,
            condition = newCondition
        )

        weatherRepository.updateWeather(updatedWeather)
    }

    fun stop() {
        isRunning = false
        scope.cancel()
    }
}