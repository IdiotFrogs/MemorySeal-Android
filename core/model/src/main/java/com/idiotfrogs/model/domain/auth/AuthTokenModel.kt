package com.idiotfrogs.model.domain.auth

data class AuthTokenModel(
    val accessToken: String,
    val refreshToken: String,
)