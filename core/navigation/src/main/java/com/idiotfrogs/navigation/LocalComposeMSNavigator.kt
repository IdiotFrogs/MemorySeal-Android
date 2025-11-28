package com.idiotfrogs.navigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalComposeMSNavigator: ProvidableCompositionLocal<MSNavigator> =
    compositionLocalOf { error("No MSNavigator provided!") }