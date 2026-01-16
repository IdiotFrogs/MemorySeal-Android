package com.idiotfrogs.network.datasource.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val accessToken: Flow<String>
    val refreshToken: Flow<String>
    val accessTokenExpiresIn: Flow<Long>

    suspend fun setTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpiresIn: Long,
    )

    suspend fun clearTokens()
}