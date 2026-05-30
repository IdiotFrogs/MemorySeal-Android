package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class RequestCollaboratorsResponse(
    val requestId: Long,
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String,
    val status: RequestStatus,
)

enum class RequestStatus { APPROVED, PENDING, REJECTED }