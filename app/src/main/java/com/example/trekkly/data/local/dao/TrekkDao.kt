package com.example.trekkly.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.trekkly.data.local.entities.TrekkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrekkDao {

    @Query("SELECT * FROM trekks ORDER BY startDate ASC")
    fun getAllTrekks(): Flow<List<TrekkEntity>>

    @Query("SELECT * FROM trekks WHERE status = :status ORDER BY startDate ASC")
    fun trekksByStatus(status: String): Flow<List<TrekkEntity>>

    @Query("SELECT COUNT(*) FROM trekks")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(trekks: List<TrekkEntity>)
}