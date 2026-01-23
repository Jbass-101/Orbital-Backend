package com.jbass.domain.model

import kotlinx.serialization.Serializable

/**Device Location*/
@Serializable
data class Zone(
    val id: String,
    val name: String
)
