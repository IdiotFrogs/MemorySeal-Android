package com.idiotfrogs.network

import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.model.auth.AuthTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login/google")
    suspend fun socialGoogleLogin(@Body body: AuthTokenRequest): AuthTokenResponse
}