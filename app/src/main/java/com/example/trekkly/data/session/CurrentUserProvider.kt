package com.example.trekkly.data.session

import com.example.trekkly.data.local.datastore.SessionPreference
import com.example.trekkly.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for "who is signed in right now".
 *
 * Login does not go through Firebase (it matches on phone number only), so
 * FirebaseAuth.currentUser is null for a logged-in returning user. Falling back
 * to the uid stored in SessionPreference keeps user-scoped Firestore work
 * running instead of silently no-opping.
 */
@Singleton
class CurrentUserProvider @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionPreference: SessionPreference
) {
    suspend fun requireUid(): String? =
        authRepository.currentUserId() ?: sessionPreference.userId.first()
}
