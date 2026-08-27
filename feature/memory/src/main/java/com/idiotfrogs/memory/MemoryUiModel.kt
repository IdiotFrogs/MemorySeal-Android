package com.idiotfrogs.memory

import androidx.compose.runtime.Immutable

@Immutable
data class MemoryCollaboratorUiModel(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String,
    val isMe: Boolean,
)

@Immutable
data class MemoryContentUiModel(
    val collaboratorIndex: Int,
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String,
    val contents: List<MemoryContentItemUiModel>,
)

@Immutable
data class MemoryContentItemUiModel(
    val contentId: Long,
    val message: String?,
    val imageUrls: List<String>,
)
