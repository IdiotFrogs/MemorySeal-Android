package com.idiotfrogs.network.authenticator

import com.idiotfrogs.model.auth.AuthTokenResponse
import com.idiotfrogs.network.BuildConfig
import com.idiotfrogs.network.datasource.local.LocalDataSource
import com.idiotfrogs.network.exception.TokenExpiredException
import com.idiotfrogs.network.util.TokenClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val json: Json,
    @TokenClient private val okHttpClient: OkHttpClient,
    private val localDataSource: LocalDataSource
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 재시도 2번 이상 실패 케이스
        if (response.count() >= 2) throw TokenExpiredException()
        val request = response.request
        val path = request.url.encodedPath
        // 리프레쉬 토큰 만료 케이스
        if (path.endsWith("auth/reissue")) throw TokenExpiredException()

        val requestToken = request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()

        synchronized(this) {
            val accessToken = runBlocking { localDataSource.accessToken.first() }
            // 이미 갱신된 토큰인 케이스
            if (!accessToken.isNullOrEmpty() && accessToken != requestToken) {
                return request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }

            val refreshToken = runBlocking { localDataSource.refreshToken.first() }
            val reissueRequest = Request.Builder()
                .url(BuildConfig.BASE_URL + "auth/reissue")
                .header("RefreshToken", "Bearer $refreshToken")
                .post("".toRequestBody()) // Body 없음
                .build()

            val reissueResponse = okHttpClient.newCall(reissueRequest).execute()

            reissueResponse.use { reissueResponse ->
                // 토큰 재발급 실패 케이스
                if (!reissueResponse.isSuccessful) {
                    runBlocking { localDataSource.clearTokens() }
                    throw TokenExpiredException()
                }
                val raw = reissueResponse.body?.string().orEmpty()
                val tokenResponse = json.decodeFromString(AuthTokenResponse.serializer(), raw)

                runBlocking { localDataSource.setTokens(tokenResponse.accessToken, tokenResponse.refreshToken) }

                return request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                    .build()
            }
        }
    }

    private fun Response.count(): Int {
        var count = 1
        var prior = this.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}