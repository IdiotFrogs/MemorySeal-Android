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

// 실제 통신용이 아님, Paging 요소를 제외한 meta
data class WateringMeta(
    val totalDays: Long,
    val wateringCount: Long,
    val stage: Int
)
