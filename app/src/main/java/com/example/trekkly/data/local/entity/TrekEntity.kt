package com.example.trekkly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trekkly.domain.model.ItineraryDay

@Entity(tableName = "treks")
data class TrekEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverImage: String,
    val images: List<String>,
    val shortDescription: String,
    val description: String,
    val difficultyLevel: String,
    val altitude: Int,
    val distance: Double,
    val days: Int,
    val nights: Int,
    val bestSeason: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val startingPoint: String,
    val endingPoint: String,
    val trekType: String,
    val rating: Double,
    val reviewCount: Int,
    val tags: List<String>,
    val isFeatured: Boolean,
    val isPopular: Boolean,
    val itinerary: List<ItineraryDay>,
    val createdAt: Long,
    val updatedAt: Long
)
