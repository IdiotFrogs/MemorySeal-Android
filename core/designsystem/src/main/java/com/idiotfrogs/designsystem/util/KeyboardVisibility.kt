package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun rememberKeyboardVisibility(): Boolean {
    val insets = WindowInsets.ime
    val density = LocalDensity.current
    val keyboardVisible by remember {
        derivedStateOf {
            with(density) { insets.getBottom(this).toDp() > 0.dp }
        }
    }

    return keyboardVisible
}
