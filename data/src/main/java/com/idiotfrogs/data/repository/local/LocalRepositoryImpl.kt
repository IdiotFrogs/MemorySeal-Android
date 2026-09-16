package com.idiotfrogs.data.repository.local

import com.idiotfrogs.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
): LocalRepository {
    override val accessToken = localDataSource.accessToken

    override suspend fun clearTokens() {
        localDataSource.clearTokens()
    }
}