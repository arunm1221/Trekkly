package com.example.trekkly.presentation.signup.uievents

import com.example.trekkly.domain.model.CountryCode

data class SignUpState(
    val fullName: String="",
    val phoneNumber: String = "",
    val countryCode: List<CountryCode> = emptyList(),
    val selectedCountryCode: CountryCode? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
){
    val isFormValid: Boolean
        get() = fullName.isNotBlank() && phoneNumber.isNotBlank() && selectedCountryCode != null
}

sealed interface SignUpEvent{
    data class NavigateToOtp(
        val verificationId: String,
        val fullPhoneNumber: String,
        val fullName: String
    ): SignUpEvent
}


