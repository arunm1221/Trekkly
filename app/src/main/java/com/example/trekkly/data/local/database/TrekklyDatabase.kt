package com.example.trekkly.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trekkly.data.local.dao.TrekkDao
import com.example.trekkly.data.local.entities.TrekkEntity
import com.example.trekkly.data.mapper.TrekkConverter

@Database(entities = [TrekkEntity::class], version = 1)
@TypeConverters(TrekkConverter::class)
abstract class TrekklyDatabase : RoomDatabase() {
    abstract fun trekkDao(): TrekkDao
}