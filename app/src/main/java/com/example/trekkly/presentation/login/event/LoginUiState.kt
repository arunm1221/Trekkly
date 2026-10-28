package com.example.trekkly.presentation.login.event

import com.example.trekkly.domain.model.CountryCode

data class LoginUiState(
    val phoneNumber: String="",
    val countryCodes: List<CountryCode> = emptyList(),
    val selectedCountryCode:CountryCode? = null,
    val isLoading: Boolean=false,
    val errorMessage: String?=null
){
    val isFormValid: Boolean get() = phoneNumber.length==10
}

sealed interface LoginEvent{
    data object NavigateToHome: LoginEvent
}
