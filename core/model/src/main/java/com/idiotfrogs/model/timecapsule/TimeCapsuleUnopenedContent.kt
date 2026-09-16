package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleUnopenedContent(
    val timeCapsuleId: Long,
    val title: String,
    val openedAt: LocalDate,
    val mainImageUrl: String,
    val stage: Int
)