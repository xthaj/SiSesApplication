package com.polstat.sisesapplication.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {

        val TOKEN = stringPreferencesKey("user_token")
        val USERNAME = stringPreferencesKey("user_username")
        val NAME = stringPreferencesKey("user_name")
        val KELAS = stringPreferencesKey("kelas")
        val STATUS_KEANGGOTAAN = stringPreferencesKey("user_statuskeanggotaan")
        val IS_ADMIN = booleanPreferencesKey("user_is_admin")
        val DIVISI = stringPreferencesKey("user_divisi")
        const val TAG = "UserPreferencesRepo"
    }

    val user: Flow<UserState> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences:", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserState(
                preferences[TOKEN] ?: "",
                preferences[USERNAME] ?: "",
                preferences[NAME] ?: "",
                preferences[KELAS] ?: "",
                preferences[STATUS_KEANGGOTAAN] ?: "",
                preferences[IS_ADMIN] ?: false,
                preferences[DIVISI] ?: ""
            )
        }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun saveUsername(name: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = name
        }
    }

    suspend fun saveName(username: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = username
        }
    }

    suspend fun saveKelas(kelas: String) {
        dataStore.edit { preferences ->
            preferences[KELAS] = kelas
        }
    }
    suspend fun saveStatusKeanggotaan(statusKeanggotaan: String) {
        dataStore.edit { preferences ->
            preferences[STATUS_KEANGGOTAAN] = statusKeanggotaan
        }
    }

    suspend fun saveIsAdmin(isAdmin: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ADMIN] = isAdmin
        }
    }

    suspend fun saveDivisi(divisi: String) {
        dataStore.edit { preferences ->
            preferences[DIVISI] = divisi
        }
    }
}

data class UserState(
    val token: String,
    val username: String,
    val name: String,
    val kelas: String,
    val statusKeanggotaan: String,
    val isAdmin: Boolean,
    val divisi: String
)