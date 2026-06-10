package com.example.trekkly.domain.usecase.signup

import com.example.trekkly.data.local.datastore.SessionPreference
import com.example.trekkly.domain.model.User
import com.example.trekkly.domain.repository.AuthRepository
import com.example.trekkly.domain.repository.UserRepository
import javax.inject.Inject

class CompleteSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionPreferences: SessionPreference
) {
    suspend operator fun invoke(verificationId: String,
                                otp: String,
                                fullName: String,
                                phoneNumber: String) : Result<User>{

        val uId = authRepository.verifyOtp(verificationId,otp)
            .getOrElse({return Result.failure(it)})

        val user = userRepository.createUserProfile(uId,fullName,phoneNumber)
            .getOrElse { return Result.failure(it) }
        sessionPreferences.markLoggedIn(uId, isProfileComplete = false)

        return Result.success(user)

    }
}