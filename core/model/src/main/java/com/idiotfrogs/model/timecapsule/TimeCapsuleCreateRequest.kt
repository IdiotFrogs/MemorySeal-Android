package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCreateRequest(
    val title: String,
    val description: String?,
)