package com.idiotfrogs.network.interceptor

import com.idiotfrogs.network.datasource.local.LocalDataSource
import com.idiotfrogs.network.util.TokenClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    @TokenClient private val okHttpClient: OkHttpClient,
    private val localDataSource: LocalDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val ignorePath = listOf("auth/login/google", "auth/reissue")
        ignorePath.forEach {
            if (path.endsWith(it)) {
                return chain.proceed(request)
            }
        }

        val accessToken = runBlocking { localDataSource.accessToken.first() }
        val authedRequest = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

       return chain.proceed(authedRequest)
    }
}