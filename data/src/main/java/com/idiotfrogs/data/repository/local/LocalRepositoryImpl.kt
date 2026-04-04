package com.idiotfrogs.data.repository.local

import com.idiotfrogs.local.LocalDataSource
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
): LocalRepository {
    override val accessToken = localDataSource.accessToken
}