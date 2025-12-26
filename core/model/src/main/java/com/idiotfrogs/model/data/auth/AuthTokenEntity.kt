package com.idiotfrogs.model.data.auth

import com.idiotfrogs.model.domain.auth.AuthTokenModel
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenEntity(
    val accessToken: String,
    val refreshToken: String,
)

fun AuthTokenEntity.toModel() = AuthTokenModel(
    accessToken = accessToken,
    refreshToken = refreshToken
)