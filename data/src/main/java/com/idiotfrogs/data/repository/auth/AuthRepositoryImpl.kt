package com.idiotfrogs.data.repository.auth

import com.idiotfrogs.model.data.auth.toModel
import com.idiotfrogs.model.domain.auth.AuthTokenModel
import com.idiotfrogs.network.AuthService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    override suspend fun authGoogle(idToken: String): AuthTokenModel {
        return JSONObject()
            .apply { put("idToken", idToken) }
            .toString()
            .toRequestBody("application/json".toMediaType())
            .let { authService.authGoogle(it).toModel() }
    }
}