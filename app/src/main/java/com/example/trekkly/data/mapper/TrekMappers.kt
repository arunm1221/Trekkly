package com.example.trekkly.data.mapper

import com.example.trekkly.data.local.entity.TrekEntity
import com.example.trekkly.domain.model.DifficultyLevel
import com.example.trekkly.domain.model.ItineraryDay
import com.example.trekkly.domain.model.Trek

fun ItineraryDayDto.toDomain(): ItineraryDay = ItineraryDay(
    dayNumber = dayNumber,
    title = title,
    description = description,
    distanceCovered = distanceCovered,
    altitudeReached = altitudeReached
)

fun TrekDto.toEntity(): TrekEntity = TrekEntity(
    id = id,
    title = title,
    coverImage = coverImage,
    images = images,
    shortDescription = shortDescription,
    description = description,
    difficultyLevel = difficultyLevel,
    altitude = altitude,
    distance = distance,
    days = days,
    nights = nights,
    bestSeason = bestSeason,
    location = location,
    latitude = latitude,
    longitude = longitude,
    startingPoint = startingPoint,
    endingPoint = endingPoint,
    trekType = trekType,
    rating = rating,
    reviewCount = reviewCount,
    tags = tags,
    isFeatured = isFeatured,
    isPopular = isPopular,
    itinerary = itinerary.map { it.toDomain() },
    createdAt = createdAt?.toDate()?.time ?: 0L,
    updatedAt = updatedAt?.toDate()?.time ?: 0L
)

fun TrekEntity.toDomain(isFavourite: Boolean): Trek = Trek(
    id = id,
    title = title,
    coverImage = coverImage,
    images = images,
    shortDescription = shortDescription,
    description = description,
    difficultyLevel = DifficultyLevel.fromString(difficultyLevel),
    altitude = altitude,
    distance = distance,
    days = days,
    nights = nights,
    bestSeason = bestSeason,
    location = location,
    latitude = latitude,
    longitude = longitude,
    startingPoint = startingPoint,
    endingPoint = endingPoint,
    trekType = trekType,
    rating = rating,
    reviewCount = reviewCount,
    tags = tags,
    isFeatured = isFeatured,
    isPopular = isPopular,
    itinerary = itinerary,
    isFavourite = isFavourite,
    createdAt = createdAt,
    updatedAt = updatedAt
)