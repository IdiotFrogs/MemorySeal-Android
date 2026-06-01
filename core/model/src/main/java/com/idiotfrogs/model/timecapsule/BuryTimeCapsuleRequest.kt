package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BuryTimeCapsuleRequest(
    val openedAt: LocalDateTime,
)