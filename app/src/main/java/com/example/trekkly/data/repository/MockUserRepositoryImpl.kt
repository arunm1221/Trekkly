package com.example.trekkly.data.repository

import com.example.trekkly.domain.model.User
import com.example.trekkly.domain.repository.UserRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockUserRepositoryImpl @Inject constructor(): UserRepository {

    private val profiles = mutableMapOf<String, User>()
    override suspend fun createUserProfile(
        uid: String,
        name: String,
        phoneNumber: String
    ): Result<User> {

        delay(500.milliseconds)

        val user = User(
            uid = uid,
            name = name,
            phoneNumber = phoneNumber,
            isProfileComplete = false
        )

        profiles[uid] = user
        return Result.success(user)

    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        delay(200.milliseconds)
        return Result.success(profiles[uid])
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        delay(300.milliseconds)
        profiles[user.uid] = user
        return Result.success(user)
    }

    override suspend fun getUserByPhone(phone: String): Result<User?> {
        val user = profiles.values.firstOrNull(){it.phoneNumber==phone}
        return Result.success(user)
    }
}