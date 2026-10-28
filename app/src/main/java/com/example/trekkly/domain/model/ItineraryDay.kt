package com.example.trekkly.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ItineraryDay(
    val dayNumber:Int,
    val title:String,
    val description: String,
    val distanceCovered: Double,
    val altitudeReached: Int
)
