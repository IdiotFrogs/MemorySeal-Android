package com.idiotfrogs.memory

import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponseData
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponseData

internal fun TimeCapsuleCollaboratorsResponseData.toUiModel(): MemoryCollaboratorUiModel {
    return MemoryCollaboratorUiModel(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        isMe = isMe,
    )
}

internal fun TimeCapsuleContentResponseData.toUiModel(
    collaboratorIndex: Int,
): MemoryContentUiModel {
    return MemoryContentUiModel(
        collaboratorIndex = collaboratorIndex,
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        contents = capsuleContents.map { it.toUiModel() },
    )
}

internal fun CapsuleContentsData.toUiModel(): MemoryContentItemUiModel {
    return MemoryContentItemUiModel(
        contentId = contentId,
        message = content,
        imageUrls = attachedFileUrls.orEmpty(),
    )
}
