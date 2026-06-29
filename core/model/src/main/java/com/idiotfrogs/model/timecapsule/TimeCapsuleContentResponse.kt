package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleContentResponse(
    val content: List<TimeCapsuleContentResponseData>,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int,
    val last: Boolean,
)

@Serializable
data class TimeCapsuleContentResponseData(
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

@Serializable
data class MyCapsuleContentsData(
    val contentId: Long,
    val content: String?,
    val attachedFiles: List<FilesData>?,
)

@Serializable
data class FilesData(
    val id: Long,
    val fileUrl: String,
    val fileType: String,
)
