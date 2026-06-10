package com.example.trekkly.domain.model

data class User(
    val uid: String,
    val name: String,
    val phoneNumber: String,
    val email: String?=null,
    val profileImageUrl: String? = null,
    val isProfileComplete: Boolean =false
)
