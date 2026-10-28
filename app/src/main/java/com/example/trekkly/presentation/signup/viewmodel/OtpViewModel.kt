package com.example.trekkly.presentation.signup.viewmodel

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.repository.AuthRepository
import com.example.trekkly.domain.usecase.signup.CompleteSignUpUseCase
import com.example.trekkly.presentation.signup.uievents.OtpEvent
import com.example.trekkly.presentation.signup.uievents.OtpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val completeSignUpUseCase: CompleteSignUpUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    companion object {
        private const val AUTO_VERIFIED_MARKER = "auto-verified"
    }

    private val verificationId: String = savedStateHandle["verificationId"]?:""
    private val phoneNumber: String = savedStateHandle["phoneNumber"]?:""
    private val fullName: String = savedStateHandle["fullName"]?:""

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private val _events = Channel<OtpEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var countDownJob: Job? = null

    init {
        startResendCountDown()
        if (verificationId == AUTO_VERIFIED_MARKER){
            verifyAndComplete()
        }
    }

    fun onDigitChanged(index:Int, value: String){
        if (value.length>1) return
        val update = _uiState.value.digits.toMutableList()
        update[index] = value.filter(Char::isDigit)
        _uiState.update { it.copy(digits=update, errorMessage = null) }
    }

    fun onVerifyClick(){
        if (!_uiState.value.isOtpComplete) return
        verifyAndComplete()
    }

    fun onResendClick(activity: Activity){
        if (!_uiState.value.canResend)return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, digits = List(6){""}) }
            authRepository.resendOtp(phoneNumber,activity)
                .onSuccess {
                    newVerificationId->
                    currentVerificationId = newVerificationId
                    _uiState.update { it.copy(isLoading = false) }
                    startResendCountDown()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Couldn't resend the code. Please try again."
                        )
                    }
                }
        }
    }
    private var currentVerificationId: String = verificationId

    private fun verifyAndComplete() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // OTP is a sign-up-only step; login matches on phone number instead.
            completeSignUpUseCase(
                verificationId = currentVerificationId,
                otp = _uiState.value.otp,
                fullName = fullName,
                phoneNumber = phoneNumber
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(OtpEvent.NavigateHome)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Verification failed. Please try again.")
                }
            }
        }
    }

    private fun startResendCountDown() {
        countDownJob?.cancel()
        _uiState.update { it.copy(resendCooldownSeconds = OtpUiState.RESEND_COOLDOWN, canResend = false) }
        countDownJob = viewModelScope.launch {
            repeat(OtpUiState.RESEND_COOLDOWN) {
                delay(1000.milliseconds)
                _uiState.update { state ->
                    val remaining = state.resendCooldownSeconds - 1
                    state.copy(
                        resendCooldownSeconds = remaining,
                        canResend = remaining == 0
                    )
                }
            }
        }
    }
}
