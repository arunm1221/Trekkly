package com.example.trekkly.data.mapper

import kotlinx.serialization.Serializable

@Serializable
data class CountryCodeDto(
    val name: String,
    val iso: String,
    val dialCode: String,
    val flag: String
)