package com.jbass.orbital.data.simulation


import com.jbass.orbital.domain.model.DeviceState
import com.jbass.orbital.domain.repository.DeviceRepository
import kotlinx.coroutines.*
import kotlin.random.Random

class SimulationEngine(
    private val deviceRepository: DeviceRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true

        scope.launch {
            while (isRunning) {
                simulateStep()
                delay(5000) // Run a simulation tick every 5 seconds
            }
        }
    }
    // Inside DeviceSimulationEngine.kt
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

    fun stop() {
        isRunning = false
        scope.cancel()
    }
}