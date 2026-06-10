package com.example.trekkly.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val Context.sessionDataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "session_preference")

class SessionPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    private object Keys{
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_PROFILE_COMPLETE = booleanPreferencesKey("is_profile_complete")
        val USER_ID = stringPreferencesKey("user_uid")
    }

    val isLoggedIn : Flow<Boolean> = dataStore.data.map { it[Keys.IS_LOGGED_IN]?:false }
    val isProfileComplete : Flow<Boolean> = dataStore.data.map { it[Keys.IS_PROFILE_COMPLETE]?:false }
    val userId : Flow<String?> = dataStore.data.map { it[Keys.USER_ID] }

    suspend fun markLoggedIn(uId: String, isProfileComplete: Boolean){
        dataStore.edit { prefs->
            prefs[Keys.IS_LOGGED_IN] = true
            prefs[Keys.IS_PROFILE_COMPLETE] = isProfileComplete
            prefs[Keys.USER_ID] = uId
        }
    }

    suspend fun markProfileComplete(){
        dataStore.edit { prefs-> prefs[Keys.IS_PROFILE_COMPLETE] = true }
    }

    suspend fun clear(){
        dataStore.edit { it.clear() }
    }
}