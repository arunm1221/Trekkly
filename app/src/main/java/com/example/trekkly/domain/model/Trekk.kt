package com.example.trekkly.domain.model

import kotlinx.serialization.Serializable

data class Trekk(
    val id: String,
    val title: String,
    val shortDescription: String,
    val location: String,
    val region: String,
    val difficulty: String,
    val durationDays: Int,
    val distanceMeters:Int,
    val maxAltitudeMeters: Int,
    val startDate:Long,
    val endDate: Long,
    val status: String,
    val tags:List<String>,
    val description: List<String>,
    val gallery: List<String>,
    val itineray: List<ItineraryDay>,
    val highlights: List<String>,
    val bestSeasons: List<String>,
    val createdAt: Long,
    val updatedAt: Long

){
    val coverImageUrl: String? get() = gallery.firstOrNull()
}

@Serializable
data class ItineraryDay(
    val day: Int,
    val title: String,
    val description: String
)
