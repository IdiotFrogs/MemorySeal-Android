package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MyTimeCapsuleResponse(
    val timeCapsuleId: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val mainImageUrl: String,
    val timeCapsuleStatus: TimeCapsuleStatus,
    val role: TimeCapsuleRole
)

enum class TimeCapsuleStatus { OPENED, BURIED, BEFOREBURIED }

enum class TimeCapsuleRole { HOST, CONTRIBUTOR }
