package com.idiotfrogs.network.service

import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.model.auth.AuthTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthService {
    @POST("auth/login/google")
    suspend fun socialGoogleLogin(@Body body: AuthTokenRequest): AuthTokenResponse

    @POST("auth/login/apple")
    suspend fun socialAppleLogin(@Body body: AuthTokenRequest): AuthTokenResponse

    @PUT("auth/fcm-token")
    suspend fun putFcmToken(@Query("fcmToken") fcmToken: String)
}