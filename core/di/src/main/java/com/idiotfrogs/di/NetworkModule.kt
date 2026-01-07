package com.idiotfrogs.di

import com.idiotfrogs.network.AuthService
import com.idiotfrogs.network.interceptor.TokenInterceptor
import com.idiotfrogs.network.util.BaseClient
import com.idiotfrogs.network.util.TokenClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesJson() = Json {}

    @Provides
    @Singleton
    @TokenClient
    fun provideOkHttpClient() = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @BaseClient
    fun providesOkHttpClient(tokenInterceptor: TokenInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { level = Level.BODY })
            .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        json: Json,
        @BaseClient okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun providesAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)
}