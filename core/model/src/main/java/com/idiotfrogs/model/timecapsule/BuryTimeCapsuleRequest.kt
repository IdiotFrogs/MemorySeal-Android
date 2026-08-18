package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BuryTimeCapsuleRequest(
    val openedAt: LocalDate,
)