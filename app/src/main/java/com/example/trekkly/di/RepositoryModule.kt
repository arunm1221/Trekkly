package com.example.trekkly.di

import com.example.trekkly.data.local.TrekkAssetDataSource
import com.example.trekkly.data.repository.CountryCodeRepositoryImpl
import com.example.trekkly.data.repository.FirebaseAuthRepositoryImpl
import com.example.trekkly.data.repository.MockUserRepositoryImpl
import com.example.trekkly.data.repository.TrekkRepositoryImpl
import com.example.trekkly.domain.repository.AuthRepository
import com.example.trekkly.domain.repository.CountryCodeRepository
import com.example.trekkly.domain.repository.TrekkRepository
import com.example.trekkly.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: FirebaseAuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(impl: MockUserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindCountryCodeRepository(impl: CountryCodeRepositoryImpl): CountryCodeRepository

    @Binds
    abstract fun bindTrekksRepository(impl: TrekkRepositoryImpl): TrekkRepository
}