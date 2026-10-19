package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.noRippleClickable(
    onClick: () -> Unit,
) = clickable(
    indication = null,
    interactionSource = null,
    onClick = onClick,
)
