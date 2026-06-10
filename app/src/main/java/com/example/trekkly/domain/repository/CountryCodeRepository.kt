package com.example.trekkly.domain.repository

import com.example.trekkly.domain.model.CountryCode

interface CountryCodeRepository {
    suspend fun getCountryCodes(): List<CountryCode>
}