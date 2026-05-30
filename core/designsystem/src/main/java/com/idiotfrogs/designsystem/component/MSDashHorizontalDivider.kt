package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun MSDashHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MSTheme.color.greyG1,
    thickness: Dp = 1.dp,
    dashWidth: Dp = 4.dp,
    gapWidth: Dp = 4.dp,
) {
    val density = LocalDensity.current
    val dashWidthPx = with(density) { dashWidth.toPx() }
    val gapWidthPx = with(density) { gapWidth.toPx() }
    val thicknessPx = with(density) { thickness.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = thicknessPx,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidthPx, gapWidthPx),
            ),
        )
    }
}
