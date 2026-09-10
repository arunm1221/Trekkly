package com.example.trekkly.domain.usecase.login

import com.example.trekkly.data.local.datastore.SessionPreference
import com.example.trekkly.domain.model.User
import com.example.trekkly.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Logs a returning user in by looking their phone number up in Firestore.
 *
 * No OTP here by design — verification happens only at sign-up. The number is
 * matched against the profile stored then; if there is no match the caller
 * surfaces "no account available".
 */
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionPreference: SessionPreference
) {
    suspend operator fun invoke(phoneNumber: String): Result<User> {

        val user = userRepository.getUserByPhone(phoneNumber)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(
                IllegalArgumentException("No account available for this number. Please sign up first.")
            )

        sessionPreference.markLoggedIn(user.uid, user.isProfileComplete)
        return Result.success(user)
    }
}
