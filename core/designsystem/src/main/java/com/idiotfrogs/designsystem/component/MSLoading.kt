package com.idiotfrogs.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@Composable
fun MSLoadingOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    dimColor: Color = Color(0x3D444444),
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180)),
        modifier = modifier
            .fillMaxSize()
            .zIndex(1f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dimColor)
                .consumePointerInput(),
            contentAlignment = Alignment.Center,
        ) {
            MSLoadingIndicator()
        }
    }
}

@Composable
fun MSLoadingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing,
            ),
        ),
    )
    val travelPx = with(LocalDensity.current) { 8.dp.toPx() }

    Row(
        modifier = modifier
            .width(75.dp)
            .height(42.dp)
            .semantics {
                contentDescription = "Loading"
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { slotIndex ->
            val phase = progress - slotIndex
            val alpha = loadingSlotAlpha(phase)
            val offsetY = loadingSlotOffsetY(
                phase = phase,
                travelPx = travelPx,
            )

            Box(
                modifier = Modifier.size(
                    width = 15.dp,
                    height = 16.dp,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_loading),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(
                            width = 15.dp,
                            height = 16.dp,
                        )
                        .graphicsLayer {
                            this.alpha = alpha
                            translationY = offsetY
                        },
                )
            }
        }
    }
}

private fun Modifier.consumePointerInput(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            event.changes.forEach { it.consume() }
        }
    }
}

private fun loadingSlotAlpha(phase: Float): Float {
    return when {
        phase < 0f || phase >= 1f -> 0f
        phase < 0.25f -> phase / 0.25f
        phase < 0.65f -> 1f
        else -> 1f - ((phase - 0.65f) / (1f - 0.65f))
    }.coerceIn(0f, 1f)
}

private fun loadingSlotOffsetY(
    phase: Float,
    travelPx: Float,
): Float {
    return when {
        phase < 0f || phase >= 1f -> travelPx
        phase < 0.25f -> {
            val fraction = FastOutSlowInEasing.transform(phase / 0.25f)
            travelPx * (1f - fraction)
        }
        phase < 0.65f -> 0f
        else -> {
            val fraction = FastOutSlowInEasing.transform(
                (phase - 0.65f) / (1f - 0.65f),
            )
            travelPx * fraction
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MSLoadingIndicatorPreview() {
    MSTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.white),
            contentAlignment = Alignment.Center,
        ) {
            MSLoadingIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MSLoadingOverlayPreview() {
    MSTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.white),
        ) {
            MSLoadingOverlay(visible = true)
        }
    }
}
