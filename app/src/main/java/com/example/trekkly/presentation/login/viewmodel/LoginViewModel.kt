package com.example.trekkly.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trekkly.domain.model.CountryCode
import com.example.trekkly.domain.repository.CountryCodeRepository
import com.example.trekkly.domain.usecase.login.LoginUseCase
import com.example.trekkly.presentation.login.event.LoginEvent
import com.example.trekkly.presentation.login.event.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val countryCodeRepository: CountryCodeRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<LoginEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadCountryCodes()
    }

    private fun loadCountryCodes() {
        viewModelScope.launch {
            val codes = countryCodeRepository.getCountryCodes()
            _uiState.update { it.copy(countryCodes = codes, selectedCountryCode = codes.firstOrNull(){it.isoCode == "IN"})}
        }
    }

    fun onCountryCodeChanged(countryCode: CountryCode){
        _uiState.update { it.copy(selectedCountryCode = countryCode) }
    }

    fun onPhoneNumberChanged(value: String){
        _uiState.update { it.copy(phoneNumber = value.filter(Char::isDigit), errorMessage = null) }
    }

    fun onLoginClicked(){
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid phone number") }
            return
        }

        // Must match the format sign-up stored: dial code + national number.
        val fullPhone = "${state.selectedCountryCode?.dialCode.orEmpty()}${state.phoneNumber}"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(phoneNumber = fullPhone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _event.send(LoginEvent.NavigateToHome)
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
