package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MyTimeCapsuleResponse(
    val timeCapsuleId: Long,
    val title: String,
    val openedAt: LocalDate? = null,
    val createdAt: LocalDate,
    val mainImageUrl: String,
    val timeCapsuleStatus: TimeCapsuleStatus,
    val role: TimeCapsuleRole,
    val stage: Int,
)

enum class TimeCapsuleStatus { OPENED, BURIED, BEFOREBURIED }

enum class TimeCapsuleRole { HOST, CONTRIBUTOR }
