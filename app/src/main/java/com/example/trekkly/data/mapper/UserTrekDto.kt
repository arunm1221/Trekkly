package com.example.trekkly.data.mapper

import com.google.firebase.Timestamp

/**
 * Firestore document at users/{uid}/treks/{trekId}.
 * All fields default so Firestore can deserialize via toObjects().
 */
data class UserTrekDto(
    val trekId: String = "",
    val status: String = "",
    val scheduleDate: Timestamp? = null,
    val startedAt: Timestamp? = null,
    val currentDay: Int = 0,
    val completedAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
