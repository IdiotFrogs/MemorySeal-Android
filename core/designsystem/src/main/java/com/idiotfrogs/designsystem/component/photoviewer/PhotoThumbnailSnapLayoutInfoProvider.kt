package com.idiotfrogs.designsystem.component.photoviewer

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
internal fun rememberPhotoThumbnailSnapLayoutInfoProvider(
    state: LazyListState,
): SnapLayoutInfoProvider {
    val minimumDirectionalVelocity = with(LocalDensity.current) { 400.dp.toPx() }
    return remember(state, minimumDirectionalVelocity) {
        PhotoThumbnailSnapLayoutInfoProvider(
            delegate = SnapLayoutInfoProvider(state, SnapPosition.Center),
            minimumDirectionalVelocity = minimumDirectionalVelocity,
        )
    }
}

internal class PhotoThumbnailSnapLayoutInfoProvider(
    private val delegate: SnapLayoutInfoProvider,
    private val minimumDirectionalVelocity: Float,
) : SnapLayoutInfoProvider {
    private var releaseVelocity = 0f

    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
        releaseVelocity = velocity
        return delegate.calculateApproachOffset(velocity, decayOffset)
    }

    override fun calculateSnapOffset(velocity: Float): Float {
        // A fast fling must keep its release direction even after deceleration.
        val targetVelocity = if (abs(releaseVelocity) >= minimumDirectionalVelocity) {
            releaseVelocity
        } else {
            velocity
        }
        return delegate.calculateSnapOffset(targetVelocity)
    }
}
