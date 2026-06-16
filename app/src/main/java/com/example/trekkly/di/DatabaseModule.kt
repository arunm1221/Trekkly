package com.example.trekkly.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trekkly.data.local.dao.TrekkDao
import com.example.trekkly.data.local.database.TrekklyDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): TrekklyDatabase =
        Room.databaseBuilder(context, TrekklyDatabase::class.java,"trekkly-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTrekkDao(db: TrekklyDatabase): TrekkDao = db.trekkDao()
}