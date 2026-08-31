package com.idiotfrogs.preview

import androidx.compose.runtime.Immutable
import com.idiotfrogs.designsystem.component.MSTimeCapsuleContentUiModel

@Immutable
data class PreviewContentUiModel(
    val contentId: Long,
    val uiModel: MSTimeCapsuleContentUiModel,
)
