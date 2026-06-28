package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleContentResponse(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String,
    val capsuleContents: List<CapsuleContentsData>,
)

@Serializable
data class CapsuleContentsData(
    val contentId: Long,
    val content: String?,
    val attachedFileUrls: List<String>?,
)
