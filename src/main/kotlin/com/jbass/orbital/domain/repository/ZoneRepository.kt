package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.zone.Zone

interface ZoneRepository {

    fun getAllZones() : List<Zone>
}