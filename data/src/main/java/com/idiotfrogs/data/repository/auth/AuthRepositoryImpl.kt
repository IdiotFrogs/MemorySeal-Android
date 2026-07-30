package com.idiotfrogs.data.repository.auth

import com.idiotfrogs.data.datasource.auth.AuthDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun putFcmToken(fcmToken: String) {
        authDataSource.putFcmToken(fcmToken)
    }

    override suspend fun logout() {
        authDataSource.logout()
    }
}
