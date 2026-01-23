package com.jbass.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionCommand(
    val subscribeZones: List<String> = emptyList(),
    val unsubscribeZones: List<String> = emptyList()
)
