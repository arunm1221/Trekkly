package com.example.trekkly.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class TrekDto(
    val id: String = "",
    val title: String = "",
    val coverImage: String = "",
    val images: List<String> = emptyList(),
    val shortDescription: String = "",
    val description: String = "",
    val difficultyLevel: String = "",
    val altitude: Int = 0,
    val distance: Double = 0.0,
    val days: Int = 0,
    val nights: Int = 0,
    val bestSeason: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val startingPoint: String = "",
    val endingPoint: String = "",
    val trekType: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val tags: List<String> = emptyList(),
    @get:PropertyName("isFeatured") @set:PropertyName("isFeatured")
    var isFeatured: Boolean = false,

    @get:PropertyName("isPopular") @set:PropertyName("isPopular")
    var isPopular: Boolean = false,
    val itinerary: List<ItineraryDayDto> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null


    )
data class ItineraryDayDto(
    val dayNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val distanceCovered: Double = 0.0,
    val altitudeReached: Int = 0
)