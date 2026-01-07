package com.idiotfrogs.di

import com.idiotfrogs.network.datasource.auth.AuthDataSource
import com.idiotfrogs.data.datasource.auth.AuthDataSourceImpl
import com.idiotfrogs.data.datasource.local.LocalDataSourceImpl
import com.idiotfrogs.network.datasource.local.LocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Binds
    fun bindsLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): LocalDataSource

    @Binds
    fun bindsAuthDataSource(
        authDataSourceImpl: AuthDataSourceImpl
    ): AuthDataSource
}