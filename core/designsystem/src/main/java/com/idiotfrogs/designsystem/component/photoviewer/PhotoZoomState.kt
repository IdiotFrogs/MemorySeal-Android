package com.idiotfrogs.designsystem.component.photoviewer

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

private const val MAX_PHOTO_SCALE = 5f
private const val ZOOM_RESET_THRESHOLD = 1.01f

@Stable
internal class PhotoZoomState {
    var scale by mutableFloatStateOf(1f)
        private set

    var offset by mutableStateOf(Offset.Zero)
        private set

    var gestureActive by mutableStateOf(false)
        private set

    val isZoomed: Boolean
        get() = scale > 1f

    private var viewport = Size.Zero
    private var fittedImage = Size.Zero

    fun updateGeometry(viewport: Size, fittedImage: Size) {
        this.viewport = viewport
        this.fittedImage = fittedImage
        offset = limitOffset(offset, scale)
    }

    fun beginGesture() {
        gestureActive = true
    }

    fun finishGesture() {
        if (scale < ZOOM_RESET_THRESHOLD) reset()
        gestureActive = false
    }

    fun transform(centroid: Offset, pan: Offset, zoomChange: Float) {
        if (viewport.width <= 0f || viewport.height <= 0f) return

        val nextScale = (scale * zoomChange).coerceIn(1f, MAX_PHOTO_SCALE)
        val ratio = nextScale / scale
        val center = Offset(viewport.width / 2f, viewport.height / 2f)
        val nextOffset = offset * ratio + (centroid - center) * (1f - ratio) + pan

        offset = limitOffset(nextOffset, nextScale)
        scale = nextScale
    }

    private fun limitOffset(value: Offset, scale: Float): Offset {
        val maxX = ((fittedImage.width * scale - viewport.width) / 2f).coerceAtLeast(0f)
        val maxY = ((fittedImage.height * scale - viewport.height) / 2f).coerceAtLeast(0f)

        return Offset(
            x = value.x.coerceIn(-maxX, maxX),
            y = value.y.coerceIn(-maxY, maxY),
        )
    }

    fun reset() {
        scale = 1f
        offset = Offset.Zero
    }
}
