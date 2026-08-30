package com.idiotfrogs.preview

import com.idiotfrogs.designsystem.component.MSTimeCapsuleContentUiModel
import com.idiotfrogs.model.timecapsule.MyCapsuleContentsData

internal fun MyCapsuleContentsData.toUiModel(): PreviewContentUiModel {
    return PreviewContentUiModel(
        contentId = contentId,
        uiModel = MSTimeCapsuleContentUiModel(
            message = content?.takeIf { it.isNotBlank() },
            imageUrls = attachedFiles
                .orEmpty()
                .map { it.fileUrl },
        ),
    )
}
