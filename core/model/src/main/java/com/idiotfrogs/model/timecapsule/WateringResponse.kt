package com.idiotfrogs.model.timecapsule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class WateringResponse(
    val totalDays: Long,
    val wateringCount: Long,
    val stage: Int,
    val waterings: WateringDataResponse
)

@Serializable
data class WateringDataResponse(
    val content: List<WateringContentResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val last: Boolean,
)

@Serializable
data class WateringContentResponse(
    val wateredDate: LocalDate,
    val isWatered: Boolean,
    val userId: Long?,
    val profileImageUrl: String?,
)