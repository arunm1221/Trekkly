package com.example.trekkly.presentation.signup.uievents


data class OtpUiState(
    val digits:List<String> = List(6){""},
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendCooldownSeconds:Int = RESEND_COOLDOWN,
    val canResend: Boolean =false
) {
    val otp: String get() = digits.joinToString("")
    val isOtpComplete: Boolean get() = digits.all{it.isNotEmpty()}

    companion object {
        const val RESEND_COOLDOWN = 60
    }
}

sealed interface OtpEvent{
    data object NavigateHome: OtpEvent
}

