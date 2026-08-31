package com.example.trekkly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local-only per-user trek record (expedition / scheduled plan).
 * Kept separate from TrekEntity so catalog sync never clobbers user state.
 * Only user-specific data lives here; title/image/days come from the joined trek.
 */
@Entity(tableName = "user_treks")
data class UserTrekEntity(
    @PrimaryKey val trekId: String,
    val status: String,
    val scheduleDate: Long? = null,
    val startedAt: Long? = null,
    val currentDay: Int = 0,
    val completedAt: Long? = null,
    val updatedAt: Long = 0L
)
