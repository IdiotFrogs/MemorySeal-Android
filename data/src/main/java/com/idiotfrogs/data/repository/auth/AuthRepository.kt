package com.idiotfrogs.data.repository.auth

import com.idiotfrogs.model.domain.auth.AuthTokenModel

interface AuthRepository {
    suspend fun authGoogle(idToken: String): AuthTokenModel
}