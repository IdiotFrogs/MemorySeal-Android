package com.idiotfrogs.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val nickname: String,
    val email: String
)