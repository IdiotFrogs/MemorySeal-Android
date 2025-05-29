package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

@Composable
fun Modifier.keyboardAutoScroll(scrollState: ScrollState): Modifier {
    val isKeyboardVisible = rememberKeyboardVisibility()

    LaunchedEffect(isKeyboardVisible) { if (isKeyboardVisible) { scrollState.scrollTo(scrollState.maxValue) } }

    return this@keyboardAutoScroll
}