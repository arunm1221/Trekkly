package com.example.trekkly.data.mapper

import com.example.trekkly.data.local.entities.TrekkEntity
import com.example.trekkly.domain.model.Trekk

fun TrekkSeed.toEntity(now: Long): TrekkEntity = TrekkEntity(
    id = id,
    title = title,
    shortDescription = shortDescription,
    location = location,
    region = region,
    difficulty = difficulty,
    durationDays = durationDays,
    distanceMeters = distanceMeters,
    maxAltitudeMeters = maxAltitudeMeters,
    startDate = startDate,
    endDate = endDate,
    status = status,
    price = price,
    currency = currency,
    tags = tags,
    description = description,
    gallery = gallery,
    itinerary = itinerary,
    highlights = highlights,
    bestSeasons = bestSeasons,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastSyncedAt = now
)

fun TrekkEntity.toDomain(): Trekk = Trekk(
    id = id,
    title = title,
    shortDescription = shortDescription,
    location = location,
    region = region,
    difficulty = difficulty,
    durationDays = durationDays,
    distanceMeters = distanceMeters,
    maxAltitudeMeters = maxAltitudeMeters,
    startDate = startDate,
    endDate = endDate,
    status = status,
    tags = tags,
    description = listOf(description),
    gallery = gallery,
    itineray = itinerary,
    highlights = highlights,
    bestSeasons = bestSeasons,
    createdAt = createdAt,
    updatedAt = updatedAt
)