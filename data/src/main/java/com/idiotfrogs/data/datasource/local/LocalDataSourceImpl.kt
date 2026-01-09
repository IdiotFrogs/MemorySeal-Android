package com.idiotfrogs.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.idiotfrogs.network.datasource.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LocalDataSource {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        private val ACCESS_TOKEN_EXPIRES_IN_KEY = longPreferencesKey("access_token_expires_in")
        private val REFRESH_TOKEN_EXPIRES_IN_KEY = longPreferencesKey("refresh_token_expired_in")
    }

    override val accessToken: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }

    override val refreshToken: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY] ?: ""
        }

    override val accessTokenExpiresIn: Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_EXPIRES_IN_KEY] ?: 0L
        }

    override val refreshTokenExpiresIn: Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_EXPIRES_IN_KEY] ?: 0L
        }

    override suspend fun setTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresIn: Long,
        refreshTokenExpiresIn: Long
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[ACCESS_TOKEN_EXPIRES_IN_KEY] = accessTokenExpiresIn
            preferences[REFRESH_TOKEN_EXPIRES_IN_KEY] = refreshTokenExpiresIn
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}