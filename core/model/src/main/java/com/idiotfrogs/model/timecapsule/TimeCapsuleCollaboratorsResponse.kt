package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCollaboratorsResponse(
    val contributorRole: TimeCapsuleRole,
    val nickname: String,
    val bury: Boolean?,
    val userId: Long,
    val profileImageUrl: String,
    val userActiveStatus: Boolean,
    val isMe: Boolean,
)