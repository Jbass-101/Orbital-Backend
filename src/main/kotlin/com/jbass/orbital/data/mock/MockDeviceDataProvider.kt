package com.jbass.orbital.data.mock

import com.jbass.orbital.domain.model.ClimateMode
import com.jbass.orbital.domain.model.DeviceCategory
import com.jbass.orbital.domain.model.DeviceMetadata
import com.jbass.orbital.domain.model.DeviceState
import com.jbass.orbital.domain.model.DeviceType
import com.jbass.orbital.domain.model.SmartDevice
import com.jbass.orbital.domain.model.Zone
import com.jbass.orbital.util.IdGenerator


import kotlin.random.Random

object MockDeviceDataProvider {

    val livingRoom = Zone(
        id = IdGenerator.newId(),
        name = "Living Room"
    )

    val bedroom = Zone(
        id = IdGenerator.newId(),
        name = "Bedroom"
    )

    val kitchen = Zone(
        id = IdGenerator.newId(),
        name = "Kitchen"
    )

    val bathroom = Zone(
        id = IdGenerator.newId(),
        name = "Bathroom"
    )

    val office = Zone(
        id = IdGenerator.newId(),
        name = "Office"
    )

    val garage = Zone(
        id = IdGenerator.newId(),
        name = "Garage"
    )

    val hallway = Zone(
        id = IdGenerator.newId(),
        name = "Hallway"
    )

    val backyard = Zone(
        id = IdGenerator.newId(),
        name = "Backyard"
    )

    val zones = listOf(livingRoom, bedroom, kitchen, bathroom, office, garage, hallway, backyard)

    val manufacturers = listOf(
        "Philips", "Nest", "Ubiquiti", "Sonos", "Apple", "Samsung", "LG", "Bosch",
        "Ring", "Arlo", "TP-Link", "Netgear", "D-Link", "Eero", "Lutron", "Ecobee",
        "Honeywell", "Yale", "August", "Chamberlain", "Somfy", "Shelly", "Nanoleaf",
        "Govee", "Wemo", "Kasa", "Meross", "Aqara", "Xiaomi", "Google"
    )

    val models = mapOf(
        DeviceType.LIGHT to listOf("Hue Bulb", "Smart LED", "Ambient Light", "Panel Light"),
        DeviceType.DIMMER to listOf("Dimmer Switch", "Smart Dimmer", "Wall Controller"),
        DeviceType.RGB_LIGHT to listOf("RGB Strip", "Color Bulb", "Gradient Light"),
        DeviceType.THERMOSTAT to listOf("Learning Thermostat", "Smart Thermostat", "WiFi Thermostat"),
        DeviceType.HVAC to listOf("Mini Split", "AC Controller", "Heat Pump"),
        DeviceType.FAN to listOf("Ceiling Fan", "Smart Fan", "Tower Fan"),
        DeviceType.TV to listOf("OLED TV", "QLED TV", "Smart TV", "4K TV"),
        DeviceType.AVR to listOf("7.1 Receiver", "5.1 Receiver", "Sound Processor"),
        DeviceType.SOUNDBAR to listOf("Dolby Atmos", "Wireless Soundbar", "Subwoofer Combo"),
        DeviceType.PROJECTOR to listOf("4K Projector", "Laser Projector", "Home Cinema"),
        DeviceType.MEDIA_PLAYER to listOf("Streaming Box", "Blu-ray Player", "Media Center"),
        DeviceType.AUDIO_ZONE to listOf("Multi-room Audio", "Zone Player", "Streaming Amp"),
        DeviceType.SPEAKER to listOf("Smart Speaker", "Bookshelf", "Outdoor Speaker"),
        DeviceType.DOOR_LOCK to listOf("Smart Lock", "Keyless Entry", "Fingerprint Lock"),
        DeviceType.GARAGE_DOOR to listOf("WiFi Opener", "Smart Hub", "Controller"),
        DeviceType.CAMERA to listOf("Security Cam", "Doorbell Cam", "PTZ Camera"),
        DeviceType.MOTION_SENSOR to listOf("PIR Sensor", "Motion Detector", "Presence Sensor"),
        DeviceType.BLIND to listOf("Motorized Blind", "Roller Shade", "Venetian Blind"),
        DeviceType.CURTAIN to listOf("Smart Curtain", "Drapery Rail", "Track System"),
        DeviceType.SMART_PLUG to listOf("Energy Monitor", "WiFi Plug", "Outlet"),
        DeviceType.ENERGY_METER to listOf("Smart Meter", "Consumption Monitor", "CT Clamp"),
        DeviceType.UPS to listOf("Battery Backup", "Power Station", "UPS System"),
        DeviceType.NETWORK_SWITCH to listOf("PoE Switch", "Managed Switch", "Gigabit Switch"),
        DeviceType.ACCESS_POINT to listOf("WiFi 6 AP", "Mesh Node", "Enterprise AP"),
        DeviceType.CONTROLLER to listOf("Hub", "Bridge", "Gateway"),
        DeviceType.SCENE to listOf("Scene Controller", "Button Panel", "Touchscreen"),
        DeviceType.VIRTUAL_BUTTON to listOf("Virtual Switch", "Automation Trigger", "Shortcut"),
        DeviceType.SYSTEM_MONITOR to listOf("Health Monitor", "Dashboard", "System Status")
    )

    val deviceNames = listOf(
        "Main", "Primary", "Secondary", "Ceiling", "Wall", "Floor", "Table",
        "Desk", "Reading", "Ambient", "Accent", "Task", "Overhead", "Recessed",
        "Track", "Flood", "Spot", "Pendant", "Chandelier", "Fan Light", "Sconce"
    )

    val roomSpecificNames = mapOf(
        "Living Room" to listOf("Entertainment", "Center", "Fireplace", "Sofa", "Coffee Table"),
        "Bedroom" to listOf("Nightstand", "Closet", "Dresser", "Bed", "Reading Nook"),
        "Kitchen" to listOf("Counter", "Island", "Sink", "Cabinet", "Pantry", "Refrigerator"),
        "Bathroom" to listOf("Vanity", "Mirror", "Shower", "Tub", "Toilet", "Sauna"),
        "Office" to listOf("Desk", "Monitor", "Workstation", "Bookshelf", "Filing"),
        "Garage" to listOf("Workbench", "Toolbox", "Vehicle", "Storage", "Workshop"),
        "Hallway" to listOf("Entry", "Stairs", "Closet", "Passage", "Foyer"),
        "Backyard" to listOf("Patio", "Deck", "Garden", "Pool", "Grill", "Porch")
    )

    private val random = Random(System.currentTimeMillis())

    val devices: List<SmartDevice> = run {
        val deviceList = mutableListOf<SmartDevice>()

        // Generate 100 devices
        repeat(100) { index ->
            val zone = zones.random()
            val deviceType = DeviceType.entries.random()
            val manufacturer = manufacturers.random()
            val model = models[deviceType]?.random() ?: "Unknown Model"

            val name = when (random.nextInt(5)) {
                0 -> "${deviceNames.random()} ${deviceType.name.replace("_", " ")}"
                1 -> "${roomSpecificNames[zone.name]?.random() ?: "Room"} ${deviceType.name.replace("_", " ")}"
                2 -> "${zone.name} ${deviceNames.random()} ${deviceType.name.replace("_", " ")}"
                3 -> "${manufacturer} ${model}"
                else -> "${deviceType.name.replace("_", " ")} ${index + 1}"
            }

            val state = when (deviceType.category) {
                DeviceCategory.LIGHTING -> when (deviceType) {
                    DeviceType.DIMMER -> DeviceState.Level(random.nextInt(20, 101))
                    DeviceType.RGB_LIGHT -> DeviceState.Level(random.nextInt(20, 101))
                    else -> DeviceState.OnOff(random.nextBoolean())
                }
                DeviceCategory.CLIMATE -> DeviceState.Temperature(
                    current = random.nextFloat() * 15 + 18, // 18-33°C
                    target = random.nextFloat() * 5 + 20, // 20-25°C
                    mode = ClimateMode.entries.random()
                )
                DeviceCategory.MEDIA -> DeviceState.Media(
                    isOn = random.nextBoolean(),
                    volume = random.nextInt(101),
                    source = listOf("HDMI", "Bluetooth", "AirPlay", "Chromecast", "Spotify").random()
                )
                DeviceCategory.AUDIO -> DeviceState.Media(
                    isOn = random.nextBoolean(),
                    volume = random.nextInt(101),
                    source = listOf("AirPlay", "Spotify", "Local", "Radio", "Line-In").random()
                )
                DeviceCategory.SECURITY -> when (deviceType) {
                    DeviceType.CAMERA -> DeviceState.OnOff(random.nextBoolean())
                    DeviceType.MOTION_SENSOR -> DeviceState.OnOff(random.nextBoolean())
                    else -> DeviceState.OnOff(random.nextBoolean())
                }
                DeviceCategory.SHADING -> DeviceState.Position(random.nextInt(101))
                DeviceCategory.ENERGY -> when (deviceType) {
                    DeviceType.SMART_PLUG -> DeviceState.OnOff(random.nextBoolean())
                    else -> DeviceState.OnOff(random.nextBoolean())
                }
                DeviceCategory.NETWORK -> DeviceState.Network(
                    online = random.nextFloat() > 0.1f, // 90% online
                    latencyMs = if (random.nextBoolean()) random.nextInt(1, 100) else null
                )
                DeviceCategory.VIRTUAL -> DeviceState.OnOff(random.nextBoolean())
            }

            val metadata = DeviceMetadata(
                manufacturer = manufacturer,
                model = model,
                firmwareVersion = "${random.nextInt(1, 10)}.${random.nextInt(0, 10)}.${random.nextInt(0, 10)}",
                ipAddress = if (random.nextBoolean()) "192.168.1.${random.nextInt(2, 255)}" else null,
                macAddress = if (random.nextBoolean()) generateMacAddress() else null,
                isReachable = random.nextFloat() > 0.15f, // 85% reachable
                lastSeenEpochMs = System.currentTimeMillis() - random.nextLong(0, 3600000) // Up to 1 hour ago
            )

            deviceList.add(
                SmartDevice(
                    id = IdGenerator.newId(),
                    name = name,
                    type = deviceType,
                    state = state,
                    zoneId = zone.id,
                    metadata = metadata
                )
            )
        }

        deviceList
    }

    private fun generateMacAddress(): String {
        return (1..6).joinToString(":") {
            random.nextInt(256).toString(16).padStart(2, '0').uppercase()
        }
    }
}