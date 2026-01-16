package com.idiotfrogs.network.interceptor

import com.idiotfrogs.model.auth.AuthTokenResponse
import com.idiotfrogs.network.BuildConfig
import com.idiotfrogs.network.datasource.local.LocalDataSource
import com.idiotfrogs.network.exception.TokenExpiredException
import com.idiotfrogs.network.util.TokenClient
import kotlinx.coroutines.flow.combine
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
    private val json: Json,
    @TokenClient private val okHttpClient: OkHttpClient,
    private val localDataSource: LocalDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val ignorePath = listOf("auth/login/google", "auth/reissue")

        if (ignorePath.any { path.endsWith(it) }) {
            return chain.proceed(request)
        }

        val tokenResponse = runBlocking { getTokenResponse() }
        val currentTime = System.currentTimeMillis()

        var currentAccessToken = tokenResponse.accessToken

        if (currentTime >= tokenResponse.accessTokenExpiresIn) {
            val reissueRequest = Request.Builder()
                .url(BuildConfig.BASE_URL + "auth/reissue")
                .header("RefreshToken", "Bearer ${tokenResponse.refreshToken}")
                .post("".toRequestBody()) // Body 없음
                .build()

            val reissueResponse = okHttpClient.newCall(reissueRequest).execute()

            reissueResponse.use { reissueResponse ->
                // 토큰 재발급 실패
                if (!reissueResponse.isSuccessful) {
                    runBlocking { localDataSource.clearTokens() }
                    throw TokenExpiredException()
                }
                val raw = reissueResponse.body?.string().orEmpty()
                val newTokenResponse = json.decodeFromString(AuthTokenResponse.serializer(), raw)

                runBlocking {
                    localDataSource.setTokens(
                        newTokenResponse.accessToken,
                        newTokenResponse.refreshToken,
                        newTokenResponse.accessTokenExpiresIn,
                    )
                }

                currentAccessToken = newTokenResponse.accessToken
            }
        }

        val authedRequest = request.newBuilder()
            .header("Authorization", "Bearer $currentAccessToken")
            .build()

        return chain.proceed(authedRequest)
    }

    private suspend fun getTokenResponse(): AuthTokenResponse {
        return combine(
            localDataSource.accessToken,
            localDataSource.refreshToken,
            localDataSource.accessTokenExpiresIn,
        ) { access, refresh, accessExp ->
            AuthTokenResponse(access, refresh, accessExp)
        }.first()
    }
}