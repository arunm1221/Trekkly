package com.example.trekkly.data.mapper

import com.example.trekkly.domain.model.ItineraryDay
import kotlinx.serialization.Serializable

@Serializable
data class TrekkSeed(
    val id: String,
    val title: String,
    val shortDescription: String,
    val location: String,
    val region: String,
    val difficulty: String,
    val durationDays: Int,
    val distanceMeters: Int,
    val maxAltitudeMeters: Int,
    val startDate: Long,
    val endDate: Long,
    val status: String,
    val price: Double,
    val currency: String,
    val tags: List<String>,
    val description: String,
    val gallery: List<String>,
    val itinerary: List<ItineraryDay>,
    val highlights: List<String>,
    val bestSeasons: List<String>,
    val createdAt: Long,
    val updatedAt: Long
)