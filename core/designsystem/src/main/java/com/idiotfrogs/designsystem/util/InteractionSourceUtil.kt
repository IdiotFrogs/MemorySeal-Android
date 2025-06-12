package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun rememberFocusState(): Pair<MutableInteractionSource, Boolean> {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    return interactionSource to isFocused
}

@Composable
fun rememberPressState(): Pair<MutableInteractionSource, Boolean> {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    return interactionSource to isPressed
}