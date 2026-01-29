package com.jbass.orbital.domain.repository

import com.jbass.orbital.domain.model.Zone

interface ZoneRepository {

    fun getAllZones() : List<Zone>
}