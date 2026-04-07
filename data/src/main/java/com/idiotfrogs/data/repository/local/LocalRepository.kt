package com.idiotfrogs.data.repository.local

import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    val accessToken: Flow<String>
}