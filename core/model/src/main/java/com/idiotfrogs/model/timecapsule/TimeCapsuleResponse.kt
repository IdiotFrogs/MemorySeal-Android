package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleResponse(
    val title: String,
    val description: String?,
    val buriedAt: LocalDate?,
    val createdAt: LocalDate,
    val openedAt: LocalDate?,
    val mainImageUrl: String,
    val timeCapsuleStatus: TimeCapsuleStatus,
    val userRole: TimeCapsuleRole,
    val myContentCount: Int,
    val myImageCount: Int,
)