package com.example.trekkly.data.repository

import android.app.Activity
import com.example.trekkly.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class MockAuthRepositoryImpl  @Inject constructor(): AuthRepository{

    private companion object{
        val MOCK_OTP = "1234"
    }
    private val pendingVerifications = mutableMapOf<String, String>()
    private var loggedUid: String? = null

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<String> {
        delay(800.milliseconds)
        val verificationId = "mock_verification-${1234}"
        pendingVerifications[verificationId] = phoneNumber
        return Result.success(verificationId)
    }

    override suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ): Result<String> {
        val phoneNumber = pendingVerifications[verificationId]?:
        return Result.failure(IllegalStateException("verification Session Expired"))

        return if (otp == MOCK_OTP){
            val uId = "uid-${phoneNumber.filter(Char::isDigit)}"
            loggedUid = uId
            pendingVerifications.remove(verificationId)
            Result.success(uId)

        }else{
            Result.failure(IllegalArgumentException("Incorrect code. Please try again."))
        }
    }

    override fun currentUserId(): String? {
       return loggedUid
    }

    override suspend fun signOut() {
       loggedUid=null
    }

    override suspend fun resendOtp(phoneNumber: String, activity: Activity): Result<String> {
        delay(800.milliseconds)
        val verificationId = "mock-verification-resend-${1234}"
        pendingVerifications[verificationId] = phoneNumber
        return Result.success(verificationId)
    }


}