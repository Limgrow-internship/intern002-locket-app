package com.intern002.locketapp.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class AuthManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { it[ACCESS_TOKEN_KEY] }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { it[REFRESH_TOKEN_KEY] }
    }

    suspend fun clearTokens() {
        dataStore.edit {
            it.clear()
        }
    }
}
