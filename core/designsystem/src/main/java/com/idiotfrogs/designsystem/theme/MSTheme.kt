package com.idiotfrogs.designsystem.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MSTheme(
    content: @Composable () -> Unit,
) {
    val msColor = MSColor()
    val view = LocalView.current
    LaunchedEffect(Unit) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
    }


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