package com.idiotfrogs.data.repository.local

import com.idiotfrogs.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
): LocalRepository {
    override val accessToken = localDataSource.accessToken

    override val capsuleIds = localDataSource.capsuleIds
        .map { ids -> ids.mapNotNull { it.toLongOrNull() }.toSet() }

    override suspend fun clearTokens() {
        localDataSource.clearTokens()
    }

    override suspend fun addCapsuleId(capsuleId: Long) {
        localDataSource.addCapsuleId(capsuleId.toString())
    }
}