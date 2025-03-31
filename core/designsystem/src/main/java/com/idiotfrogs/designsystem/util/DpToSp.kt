package com.idiotfrogs.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toSp() = with(LocalDensity.current) { this@toSp.toSp() }