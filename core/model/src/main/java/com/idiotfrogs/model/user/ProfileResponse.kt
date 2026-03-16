package com.idiotfrogs.model.user

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String,
    val email: String,
    val isOnboarding: Boolean,
)