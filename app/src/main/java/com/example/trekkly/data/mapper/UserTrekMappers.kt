package com.example.trekkly.data.mapper

import com.example.trekkly.data.local.entity.UserTrekEntity
import com.example.trekkly.domain.model.Trek
import com.example.trekkly.domain.model.TrekStatus
import com.example.trekkly.domain.model.UserTrek

fun UserTrekDto.toEntity(): UserTrekEntity = UserTrekEntity(
    trekId = trekId,
    status = status,
    scheduleDate = scheduleDate?.toDate()?.time,
    startedAt = startedAt?.toDate()?.time,
    currentDay = currentDay,
    completedAt = completedAt?.toDate()?.time,
    updatedAt = updatedAt?.toDate()?.time ?: 0L
)

fun UserTrekEntity.toDomain(trek: Trek): UserTrek = UserTrek(
    trek = trek,
    status = TrekStatus.fromString(status),
    scheduleDate = scheduleDate,
    startedAt = startedAt,
    currentDay = currentDay,
    completedAt = completedAt,
    updatedAt = updatedAt
)
