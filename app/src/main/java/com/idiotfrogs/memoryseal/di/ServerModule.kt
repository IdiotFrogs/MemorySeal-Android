package com.idiotfrogs.memoryseal.di

import com.idiotfrogs.memoryseal.BuildConfig
import com.idiotfrogs.network.util.ApiBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {
    @Provides
    @ApiBaseUrl
    fun provideApiBaseUrl(): String = BuildConfig.BASE_URL
}
