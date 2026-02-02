package com.jbass.orbital.domain.model.simulation

// src/main/kotlin/com/jbass/orbital/domain/simulation/SimulationTask.kt
enum class SimulationType {
    SENSOR_DRIFT,   // Subtle changes in temp/humidity
    BATTERY_DRAIN,  // Constant decay
    NETWORK_FLAKY,  // Random offline/online flips
    STRESS_TEST     // Rapid state changes
}