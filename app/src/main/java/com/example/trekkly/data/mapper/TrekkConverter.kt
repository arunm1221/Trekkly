package com.example.trekkly.data.mapper

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.trekkly.domain.model.ItineraryDay
import kotlinx.serialization.json.Json

class TrekkConverter {
    private val json = Json { ignoreUnknownKeys=true }

    @TypeConverter fun fromStringList(value: List<String>): String = json.encodeToString(value)
    @TypeConverter fun toStringList(value: String): List<String> = json.decodeFromString(value)

    @TypeConverter fun fromItineray(value: List<ItineraryDay>): String = json.encodeToString(value)
    @TypeConverter fun toItineray(value: String): List<ItineraryDay> = json.decodeFromString(value)
}