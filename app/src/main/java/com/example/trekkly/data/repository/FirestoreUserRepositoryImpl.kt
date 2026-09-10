package com.example.trekkly.data.repository

import com.example.trekkly.data.remote.UserRemoteDataSource
import com.example.trekkly.domain.model.User
import com.example.trekkly.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore-backed user profiles. Replaces MockUserRepositoryImpl, whose
 * in-memory map lost every profile when the process died.
 */
@Singleton
class FirestoreUserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun createUserProfile(
        uid: String,
        name: String,
        phoneNumber: String
    ): Result<User> = runCatching {
        // Firebase reuses the same uid for a phone number, so a repeat sign-up
        // should keep the existing profile rather than reset it.
        val existing = remoteDataSource.getUser(uid)
        val user = existing?.copy(name = name, phoneNumber = phoneNumber)
            ?: User(uid = uid, name = name, phoneNumber = phoneNumber, isProfileComplete = false)
        remoteDataSource.upsertUser(user, isNew = existing == null)
        user
    }

    override suspend fun getUserProfile(uid: String): Result<User?> =
        runCatching { remoteDataSource.getUser(uid) }

    override suspend fun updateUserProfile(user: User): Result<User> = runCatching {
        remoteDataSource.upsertUser(user, isNew = false)
        user
    }

    override suspend fun getUserByPhone(phone: String): Result<User?> =
        runCatching { remoteDataSource.findByPhone(phone) }
}
