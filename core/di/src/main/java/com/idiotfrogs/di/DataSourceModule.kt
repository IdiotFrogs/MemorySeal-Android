package com.idiotfrogs.di

import com.idiotfrogs.local.LocalDataSource
import com.idiotfrogs.local.LocalDataSourceImpl
import com.idiotfrogs.data.datasource.auth.AuthDataSource
import com.idiotfrogs.data.datasource.auth.AuthDataSourceImpl
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