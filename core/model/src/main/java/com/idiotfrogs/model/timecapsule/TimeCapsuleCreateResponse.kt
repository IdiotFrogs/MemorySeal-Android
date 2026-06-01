package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCreateResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val openedAt: LocalDateTime?,
    val timeCapsuleStatus: String,
    val mainImageUrl: String
)