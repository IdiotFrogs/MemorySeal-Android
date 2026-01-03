package com.idiotfrogs.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenRequest(
    val idToken: String,
)