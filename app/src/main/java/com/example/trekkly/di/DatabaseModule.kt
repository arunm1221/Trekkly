package com.example.trekkly.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trekkly.data.local.dao.TrekDao
import com.example.trekkly.data.local.database.TrekDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTrekDatabase(@ApplicationContext context: Context): TrekDatabase =
        Room.databaseBuilder(
            context, TrekDatabase::class.java,
            "trekkly.db"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideTrekDao(database: TrekDatabase): TrekDao  = database.trekDao()
}