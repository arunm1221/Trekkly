package com.example.trekkly.presentation.signup.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.model.CountryCode
import com.example.trekkly.domain.repository.CountryCodeRepository
import com.example.trekkly.domain.usecase.SendOtpUseCase
import com.example.trekkly.presentation.signup.uievents.SignUpEvent
import com.example.trekkly.presentation.signup.uievents.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val countryCodeRepository: CountryCodeRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow<SignUpState>(SignUpState())
     val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignUpEvent>(Channel.BUFFERED)
     val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCountryCodes()
    }

    private fun loadCountryCodes() {
        viewModelScope.launch {
            val codes = countryCodeRepository.getCountryCodes()
            _uiState.update {
                state ->
                state.copy(countryCode = codes,
                    selectedCountryCode = state.selectedCountryCode?:codes.firstOrNull(){it.isoCode=="IN"}?:codes.firstOrNull())
            }
        }
    }

    fun onFullNameChanged(value: String){
        _uiState.update { state -> state.copy(fullName = value, errorMessage = null) }
    }

    fun onPhoneNumberChanged(value: String){
        _uiState.update { state -> state.copy(phoneNumber = value.filter(Char::isDigit), errorMessage = null) }
    }

    fun onCountrySelected(countryCode: CountryCode){
        _uiState.update { state -> state.copy(selectedCountryCode = countryCode, errorMessage = null) }
    }

    fun onSignUpClicked(activity: Activity) {
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Please enter your name and phone number") }
            return
        }

        val dialCode = state.selectedCountryCode?.dialCode.orEmpty()
        val nationalNumber = state.phoneNumber

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            sendOtpUseCase(dialCode, nationalNumber, activity)
                .onSuccess { verificationId ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(
                        SignUpEvent.NavigateToOtp(
                            verificationId = verificationId,
                            fullPhoneNumber = "$dialCode$nationalNumber",
                            fullName = state.fullName.trim()
                        )
                    )
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Something went wrong. Please try again."
                        )
                    }
                }
        }
    }


}