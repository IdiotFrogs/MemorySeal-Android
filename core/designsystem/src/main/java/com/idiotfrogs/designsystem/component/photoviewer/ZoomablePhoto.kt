package com.idiotfrogs.designsystem.component.photoviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import kotlin.math.roundToInt

@Composable
internal fun ZoomablePhoto(
    painter: Painter,
    zoomState: PhotoZoomState,
    contentDescription: String,
    canStartZoom: () -> Boolean,
) {
    val currentCanStartZoom by rememberUpdatedState(canStartZoom)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .semantics {
                stateDescription = "확대 ${(zoomState.scale * 100f).roundToInt()}%"
            }
            .pointerInput(zoomState) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    var ownsGesture = zoomState.isZoomed
                    var rejectedGesture = false

                    try {
                        do {
                            val event = awaitPointerEvent()
                            val pointerCount = event.changes.count { it.pressed }

                            if (!ownsGesture && !rejectedGesture && pointerCount >= 2) {
                                // Finish an existing page swipe before accepting a new pinch.
                                if (currentCanStartZoom()) {
                                    ownsGesture = true
                                } else {
                                    rejectedGesture = true
                                }
                            }

                            if (ownsGesture) {
                                zoomState.beginGesture()
                                val centroid = event.calculateCentroid(useCurrent = false)

                                if (centroid.isSpecified) {
                                    zoomState.transform(
                                        centroid = centroid,
                                        pan = event.calculatePan(),
                                        zoomChange = event.calculateZoom(),
                                    )
                                }

                                event.changes.forEach { it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    } finally {
                        if (ownsGesture) zoomState.finishGesture()
                    }
                }
            },
    ) {
        val viewport = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val source = painter.intrinsicSize
        val fittedImage = if (
            source.width > 0f && source.height > 0f &&
            source.width.isFinite() && source.height.isFinite()
        ) {
            val fitScale = minOf(viewport.width / source.width, viewport.height / source.height)
            Size(source.width * fitScale, source.height * fitScale)
        } else {
            viewport
        }

        SideEffect {
            zoomState.updateGeometry(viewport, fittedImage)
        }

        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomState.scale
                    scaleY = zoomState.scale
                    translationX = zoomState.offset.x
                    translationY = zoomState.offset.y
                },
        )
    }
}
