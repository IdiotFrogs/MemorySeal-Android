package com.idiotfrogs.network.interceptor

import com.idiotfrogs.model.auth.AuthTokenResponse
import com.idiotfrogs.network.BuildConfig
import com.idiotfrogs.network.datasource.local.LocalDataSource
import com.idiotfrogs.network.exception.TokenExpiredException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val localDataSource: LocalDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val ignorePath = listOf("auth")
        ignorePath.forEach {
            if (path.contains(it)) {
                return chain.proceed(request)
            }
        }

        val accessToken = runBlocking { localDataSource.accessToken.first() }
        val authedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(authedRequest)

        if (response.code != 401) return response

        response.close() // 리소스 누수 방지

        synchronized(this) {
            val currentAccessToken = runBlocking { localDataSource.accessToken.first() }
            if (accessToken != currentAccessToken) return response // 이미 갱신됨

            val okHttpClient = OkHttpClient()
            val refreshToken = runBlocking { localDataSource.refreshToken.first() }
            if (refreshToken.isNullOrEmpty()) throw TokenExpiredException()

            val reissueRequest = Request.Builder()
                .url(BuildConfig.BASE_URL + "auth/reissue")
                .addHeader("RefreshToken", "Bearer $refreshToken")
                .post("".toRequestBody()) // Body 없음
                .build()

            val reissueResponse = okHttpClient.newCall(reissueRequest).execute()

            return reissueResponse.use { response ->
                val raw = response.body?.string().orEmpty()
                val json = Json {}
                if (response.isSuccessful) {
                    val tokenResponse = json.decodeFromString(AuthTokenResponse.serializer(), raw)
                    runBlocking { localDataSource.setTokens(tokenResponse.accessToken, tokenResponse.refreshToken) }

                    val retryRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer ${tokenResponse.accessToken}")
                        .build()

                    chain.proceed(retryRequest)
                } else {
                    throw TokenExpiredException()
                }
            }
        }
    }
}