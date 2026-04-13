package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleResponse(
    val title: String,
    val description: String?,
    val buriedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val openedAt: LocalDateTime,
    val mainImageUrl: String,
    val timeCapsuleStatus: TimeCapsuleStatus,
    val userRole: TimeCapsuleRole,
)