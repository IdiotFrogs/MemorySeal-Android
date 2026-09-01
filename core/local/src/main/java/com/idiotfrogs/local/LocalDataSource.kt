package com.idiotfrogs.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val accessToken: Flow<String>
    val refreshToken: Flow<String>
    val accessTokenExpiresIn: Flow<Long>

    val capsuleIds: Flow<Set<String>>

    suspend fun setTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresIn: Long,
    )

    suspend fun clearTokens()

    suspend fun addCapsuleId(capsuleId: String)
}