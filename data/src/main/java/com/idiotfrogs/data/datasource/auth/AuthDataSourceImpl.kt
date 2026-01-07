package com.idiotfrogs.data.datasource.auth

import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.model.auth.AuthTokenResponse
import com.idiotfrogs.network.AuthService
import com.idiotfrogs.network.datasource.auth.AuthDataSource
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : AuthDataSource {
    override suspend fun socialGoogleLogin(authTokenRequest: AuthTokenRequest): AuthTokenResponse {
        return authService.socialGoogleLogin(authTokenRequest)
    }
}