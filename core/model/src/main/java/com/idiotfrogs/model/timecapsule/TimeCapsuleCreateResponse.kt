package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCreateResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val openedAt: LocalDate?,
    val timeCapsuleStatus: String,
    val mainImageUrl: String
)