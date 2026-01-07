package com.idiotfrogs.network.datasource.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>

    suspend fun setTokens(accessToken: String, refreshToken: String)
}