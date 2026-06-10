package com.example.trekkly.domain.usecase.signup

import android.app.Activity
import com.example.trekkly.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        dialCode: String,
        nationalNumber: String,
        activity: Activity
    ): Result<String> {
        if (nationalNumber.length < MIN_NATIONAL_NUMBER_LENGTH) {
            return Result.failure(IllegalArgumentException("Enter a valid phone number."))
        }
        return authRepository.sendOtp("$dialCode$nationalNumber", activity)
    }

    private companion object {
        const val MIN_NATIONAL_NUMBER_LENGTH = 6
    }
}