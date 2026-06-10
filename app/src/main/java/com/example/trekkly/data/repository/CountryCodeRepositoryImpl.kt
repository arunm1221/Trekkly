package com.example.trekkly.data.repository

import com.example.trekkly.data.local.CountryCodeAssetDataSource
import com.example.trekkly.domain.model.CountryCode
import com.example.trekkly.domain.repository.CountryCodeRepository
import javax.inject.Inject

class CountryCodeRepositoryImpl @Inject constructor(
    private val assetDataSource: CountryCodeAssetDataSource
) : CountryCodeRepository{
    private var cache: List<CountryCode>? = null

    override suspend fun getCountryCodes(): List<CountryCode> {
        cache?.let { return it }

        val mapped = assetDataSource.loadCountryCodes().map { dto->
            CountryCode(
                countryName = dto.name,
                isoCode = dto.iso,
                dialCode = dto.dialCode,
                flagEmoji = dto.flag
            )
        }
        cache = mapped
        return mapped
    }

}