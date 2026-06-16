package com.example.trekkly.domain.repository

import com.example.trekkly.domain.model.Trekk
import kotlinx.coroutines.flow.Flow

interface TrekkRepository {

    fun getTrekks(): Flow<List<Trekk>>
    fun observeByStatus(status: String): Flow<List<Trekk>>
    suspend fun seedIfEmpty()
}