package com.jbass.orbital.data.repository

import com.jbass.orbital.domain.model.Zone
import com.jbass.orbital.domain.repository.ZoneRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryZoneRepository (initialZones: List<Zone>
    ) : ZoneRepository {

    private val zones = ConcurrentHashMap<String, Zone>()
    init {
        initialZones.forEach { zones[it.id] = it }
    }
    override fun getAllZones(): List<Zone> =
        zones.values.toList()
}

