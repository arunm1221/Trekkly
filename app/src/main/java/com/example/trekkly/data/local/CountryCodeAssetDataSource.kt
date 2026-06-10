package com.example.trekkly.data.local

import android.content.Context
import com.example.trekkly.data.mapper.CountryCodeDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class CountryCodeAssetDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json{ignoreUnknownKeys =true}

    suspend fun loadCountryCodes(): List<CountryCodeDto> = withContext(Dispatchers.IO){
        val raw = context.assets.open("country_code.json").use{
            stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        }
        json.decodeFromString(raw)
    }
}