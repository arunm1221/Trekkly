package com.example.trekkly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.trekkly.data.local.entity.FavouriteEntity
import com.example.trekkly.data.local.entity.TrekEntity
import com.example.trekkly.data.local.entity.UserTrekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrekDao {

    @Query("SELECT * FROM treks ORDER BY isFeatured DESC, rating DESC")
    fun observeTreks(): Flow<List<TrekEntity>>

    @Query("SELECT * FROM treks WHERE id= :id")
    fun observeTrekById(id: String): Flow<TrekEntity>

    @Insert(onConflict = REPLACE)
    suspend fun upsertTreks(treks: List<TrekEntity>)

    @Query("SELECT MAX(updatedAt) FROM treks")
    suspend fun getLatestUpdatedAt(): Long?

    @Query("SELECT COUNT(*) FROM treks")
    suspend fun count(): Int

    @Query("SELECT trekId FROM favourites")
    fun observeFavoritesIds() : Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE trekId = :trekId)")
    fun isFavourite(trekId: String): Flow<Boolean>

    @Insert(onConflict = REPLACE)
    suspend fun addFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE trekId =:trekId")
    suspend fun removeFavourites(trekId: String)

    // ---- User treks (per-user expeditions) ----
    @Query("SELECT * FROM user_treks")
    fun observeUserTreks(): Flow<List<UserTrekEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun upsertUserTreks(items: List<UserTrekEntity>)

    @Query("DELETE FROM user_treks")
    suspend fun clearUserTreks()

    @Query("DELETE FROM user_treks WHERE trekId = :trekId")
    suspend fun deleteUserTrek(trekId: String)

}