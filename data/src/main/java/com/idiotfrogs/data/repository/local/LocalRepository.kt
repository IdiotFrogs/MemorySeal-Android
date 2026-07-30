package com.idiotfrogs.data.repository.local

import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    val accessToken: Flow<String>
    val capsuleIds: Flow<Set<Long>>

    suspend fun clearTokens()
    suspend fun addCapsuleId(capsuleId: Long)
}