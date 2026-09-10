package com.example.trekkly.presentation.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.data.local.datastore.SessionPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Resolves where the splash screen should hand off to.
 *
 * The stored session is the authority. Login does not sign in through Firebase,
 * so FirebaseAuth.currentUser cannot be used as the test here — it would log
 * every returning user straight back out. The uid must also be present, since
 * user-scoped Firestore reads need it.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionPreference: SessionPreference
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)

    /** null while the session is still being resolved. */
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val flagged = sessionPreference.isLoggedIn.first()
            val uid = sessionPreference.userId.first()

            if (flagged && uid.isNullOrBlank()) {
                // Half-written session — clear it rather than land on a broken home.
                sessionPreference.clear()
            }
            _isLoggedIn.value = flagged && !uid.isNullOrBlank()
        }
    }
}
