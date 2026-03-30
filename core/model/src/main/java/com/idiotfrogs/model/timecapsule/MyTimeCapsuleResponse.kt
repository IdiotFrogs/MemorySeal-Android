package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MyTimeCapsuleResponse(
    val timeCapsuldId: Long,
    val title: String,
    val openAt: LocalDateTime,
    val timeCapsuleStatus: TimeCapsuleStatus,
    val role: TimeCapsuleRole
)

enum class TimeCapsuleStatus { OPENED, BURIED, BEFOREBURIED }

enum class TimeCapsuleRole { HOST, CONTRIBUTOR }
