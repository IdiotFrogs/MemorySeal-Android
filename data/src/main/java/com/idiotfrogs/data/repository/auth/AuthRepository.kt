package com.idiotfrogs.data.repository.auth

interface AuthRepository {
    suspend fun putFcmToken(fcmToken: String)
    suspend fun logout()
}
