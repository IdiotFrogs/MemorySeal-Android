package com.idiotfrogs.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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

        private val CAPSULE_ID_KEY = stringSetPreferencesKey("capsule_id")

        // 디바이스 단위 튜토리얼 시청 여부 (최초 1회만 노출)
        private val TUTORIAL_COMPLETED_KEY = booleanPreferencesKey("tutorial_completed")
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

    override val capsuleIds: Flow<Set<String>> =
        dataStore.data.map { preferences ->
            preferences[CAPSULE_ID_KEY] ?: emptySet()
        }

    override val tutorialCompleted: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[TUTORIAL_COMPLETED_KEY] ?: false
        }

    override suspend fun setTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresIn: Long,
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[ACCESS_TOKEN_EXPIRES_IN_KEY] = accessTokenExpiresIn
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(ACCESS_TOKEN_EXPIRES_IN_KEY)
        }
    }

    override suspend fun addCapsuleId(capsuleId: String) {
        dataStore.edit { preferences ->
            preferences[CAPSULE_ID_KEY] =
                (preferences[CAPSULE_ID_KEY] ?: emptySet()) + capsuleId
        }
    }

    override suspend fun setTutorialCompleted() {
        dataStore.edit { preferences ->
            preferences[TUTORIAL_COMPLETED_KEY] = true
        }
    }
}