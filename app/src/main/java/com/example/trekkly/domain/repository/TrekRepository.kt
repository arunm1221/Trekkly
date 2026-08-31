package com.example.trekkly.domain.repository

import com.example.trekkly.domain.model.Trek
import com.example.trekkly.domain.model.UserTrek
import kotlinx.coroutines.flow.Flow

interface TrekRepository {

    // Catalog
    fun observeTreks(): Flow<List<Trek>>
    suspend fun syncTrek()
    suspend fun setFavourite(trekId: String, favourite: Boolean)

    // Expeditions (per-user)
    fun observeActiveExpedition(): Flow<UserTrek?>
    fun observeUpcomingTreks(): Flow<List<UserTrek>>
    suspend fun syncUserTreks()
    suspend fun scheduleTrek(trekId: String, scheduleDate: Long)
    suspend fun startTrek(trekId: String)
    suspend fun updateProgress(trekId: String, currentDay: Int)
    suspend fun completeTrek(trekId: String)
    suspend fun removeUserTrek(trekId: String)
}
