package com.idiotfrogs.di

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}