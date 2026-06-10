package com.example.trekkly.domain.model

data class OtpSession(
    val verificationId: String,
    val resendToken: Any
)
