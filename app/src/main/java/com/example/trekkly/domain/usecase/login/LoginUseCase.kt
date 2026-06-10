package com.example.trekkly.domain.usecase.login

import com.example.trekkly.data.local.datastore.SessionPreference
import com.example.trekkly.domain.model.User
import com.example.trekkly.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionPreference: SessionPreference
) {

    suspend operator fun invoke(phoneNumber:String):Result<User?> {

        val user = userRepository.getUserByPhone(phoneNumber).
        getOrElse { return Result.failure(it) }
            ?:return Result.failure(IllegalArgumentException("No account available"))

        sessionPreference.markLoggedIn(user.uid,false)
        return Result.success(user)

    }
}