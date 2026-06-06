package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCollaboratorsResponse(
    val content: List<TimeCapsuleCollaboratorsResponseData>,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int,
    val last: Boolean,
)

@Serializable
data class TimeCapsuleCollaboratorsResponseData(
    val contributorRole: TimeCapsuleRole,
    val nickname: String,
    val userId: Long,
    val profileImageUrl: String,
    val userActiveStatus: Boolean,
    val isMe: Boolean,
)