package com.idiotfrogs.data.datasource.auth

import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.model.auth.AuthTokenResponse

interface AuthDataSource {
    suspend fun socialGoogleLogin(authTokenRequest: AuthTokenRequest): AuthTokenResponse
    suspend fun socialAppleLogin(authTokenRequest: AuthTokenRequest): AuthTokenResponse
}