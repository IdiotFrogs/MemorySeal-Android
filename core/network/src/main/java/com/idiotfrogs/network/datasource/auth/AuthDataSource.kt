package com.idiotfrogs.network.datasource.auth

import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.model.auth.AuthTokenResponse

interface AuthDataSource {
    suspend fun socialGoogleLogin(authTokenRequest: AuthTokenRequest): AuthTokenResponse
}