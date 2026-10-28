package com.example.trekkly.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trekkly.data.local.dao.TrekDao
import com.example.trekkly.data.local.entity.FavouriteEntity
import com.example.trekkly.data.local.entity.TrekEntity
import com.example.trekkly.data.local.entity.UserTrekEntity
import com.example.trekkly.data.mapper.TrekTypeConverters

@Database(
    entities = [TrekEntity::class, FavouriteEntity::class, UserTrekEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TrekTypeConverters::class)
abstract class TrekDatabase: RoomDatabase() {
    abstract fun trekDao(): TrekDao
}