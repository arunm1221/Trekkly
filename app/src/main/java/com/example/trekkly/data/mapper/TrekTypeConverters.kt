package com.example.trekkly.data.mapper

import androidx.room.TypeConverter
import com.example.trekkly.domain.model.ItineraryDay
import kotlinx.serialization.json.Json

class TrekTypeConverters {
    private val json = Json { ignoreUnknownKeys=true }
    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else json.decodeFromString(value)

    @TypeConverter
    fun fromItineraryList(value: List<ItineraryDay>): String = json.encodeToString(value)

    @TypeConverter
    fun toItineraryList(value: String): List<ItineraryDay> =
        if (value.isBlank()) emptyList() else json.decodeFromString(value)
}