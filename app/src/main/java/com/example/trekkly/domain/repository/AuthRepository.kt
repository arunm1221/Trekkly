package com.example.trekkly.domain.repository

import android.app.Activity

interface AuthRepository {

    suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<String>

    suspend fun resendOtp(phoneNumber: String, activity: Activity): Result<String>

    suspend fun verifyOtp(verificationId: String, otp: String): Result<String>

    fun currentUserId(): String?

    suspend fun signOut()
}