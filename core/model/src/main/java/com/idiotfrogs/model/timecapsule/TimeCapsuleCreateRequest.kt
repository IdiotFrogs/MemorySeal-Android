package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TimeCapsuleCreateRequest(
    val title: String,
    val description: String,
    val openedAt: LocalDateTime
)