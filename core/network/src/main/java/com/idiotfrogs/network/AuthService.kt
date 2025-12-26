package com.idiotfrogs.network

import com.idiotfrogs.model.data.auth.AuthTokenEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login/google")
    suspend fun authGoogle(
        @Body body: RequestBody
    ): AuthTokenEntity
}