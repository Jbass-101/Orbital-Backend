package com.jbass.data.mock

import com.jbass.domain.model.ClimateMode
import com.jbass.domain.model.DeviceMetadata
import com.jbass.domain.model.DeviceState
import com.jbass.domain.model.DeviceType
import com.jbass.domain.model.SmartDevice
import com.jbass.domain.model.Zone
import com.jbass.util.IdGenerator


object MockDataProvider {

    val livingRoom = Zone(
        id = IdGenerator.newId(),
        name = "Living Room"
    )

    val bedroom = Zone(
        id = IdGenerator.newId(),
        name = "Bedroom"
    )

    val devices = listOf(
        SmartDevice(
            id = IdGenerator.newId(),
            name = "Living Room Ceiling Light",
            type = DeviceType.DIMMER,
            state = DeviceState.Level(75),
            zoneId = livingRoom.id,
            metadata = DeviceMetadata(
                manufacturer = "Philips",
                model = "Hue Downlight",
                firmwareVersion = "1.2.3",
                isReachable = true,
                lastSeenEpochMs = System.currentTimeMillis()
            )
        ),
        SmartDevice(
            id = IdGenerator.newId(),
            name = "Bedroom Thermostat",
            type = DeviceType.THERMOSTAT,
            state = DeviceState.Temperature(
                current = 21f,
                target = 23f,
                mode = ClimateMode.HEAT
            ),
            zoneId = bedroom.id,
            metadata = DeviceMetadata(
                manufacturer = "Nest",
                model = "Learning Thermostat",
                firmwareVersion = "5.9.1",
                ipAddress = "192.168.1.45",
                isReachable = true,
                lastSeenEpochMs = System.currentTimeMillis()
            )
        ),
        SmartDevice(
            id = IdGenerator.newId(),
            name = "Core Network Switch",
            type = DeviceType.NETWORK_SWITCH,
            state = DeviceState.Network(
                online = false,
                latencyMs = null
            ),
            zoneId = livingRoom.id,
            metadata = DeviceMetadata(
                manufacturer = "Ubiquiti",
                model = "USW-24",
                firmwareVersion = "7.3.0",
                ipAddress = "192.168.1.1",
                isReachable = false,
                lastSeenEpochMs = System.currentTimeMillis() - 120_000
            )
        )
    )
}
