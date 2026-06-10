package com.example.trekkly.domain.repository

import com.example.trekkly.domain.model.User

interface UserRepository {

    suspend fun createUserProfile(uid: String,name: String,phoneNumber: String) : Result<User>
    suspend fun getUserProfile(uid: String): Result<User?>
    suspend fun updateUserProfile(user: User): Result<User>
}