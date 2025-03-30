package com.idiotfrogs.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MSTheme(
    content: @Composable () -> Unit,
) {
    val msColor = MSColor()

    CompositionLocalProvider(
        LocalMSColor provides msColor
    ) {
        content()
    }
}

object MSTheme {
    val color: MSColor
        @Composable
        get() = LocalMSColor.current
}