package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

fun Modifier.wavyStroke(
    color: Color,
    strokeWidth: Dp = 3.dp,
    cornerRadius: Dp = 12.dp,
    amplitude: Dp = 2.dp,   // wavy가 얼마나 바깥/안쪽으로 흔들릴지 정하는 값입니다.
    spacing: Dp = 6.dp,     // 값이 작으면 촘촘하게 흔들리고, 값이 크면 완만하게 흔들립니다.
    seed: Long = 0xC0FFEE_BABEL,
    fillColor: Color? = null,
    contentPadding: Dp = 0.dp,
    clipContent: Boolean = false,
): Modifier = this.then(
    Modifier
        .drawWithCache {
            val strokePx = strokeWidth.toPx()
            val ampPx = amplitude.toPx()
            val spacingPx = spacing.toPx().coerceAtLeast(1f)

            val pathInset = ampPx + strokePx / 2f

            val rect = Rect(
                left = pathInset,
                top = pathInset,
                right = size.width - pathInset,
                bottom = size.height - pathInset,
            )

            val radius = (cornerRadius.toPx() - pathInset)
                .coerceAtLeast(0f)
                .coerceAtMost(min(rect.width, rect.height) / 2f)

            val path = makeWavyPath(
                rect = rect,
                cornerRadius = radius,
                spacing = spacingPx,
                amplitude = ampPx,
                seed = seed + size.width.roundToInt() + size.height.roundToInt() * 1_000_003L,
            )

            onDrawWithContent {
                fillColor?.let {
                    drawPath(path = path, color = it)
                }

                if (clipContent) {
                    clipPath(path) {
                        this@onDrawWithContent.drawContent()
                    }
                } else {
                    drawContent()
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = strokePx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
        .padding(contentPadding)
)



private data class WavyData(val point: Offset, val normal: Offset)

private fun makeWavyPath(
    rect: Rect,
    cornerRadius: Float,
    spacing: Float,
    amplitude: Float,
    seed: Long,
): Path {
    val wavyData = mutableListOf<WavyData>()

    fun line(from: Offset, to: Offset, normal: Offset) {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val length = hypot(dx, dy)
        val count = max(1, (length / spacing).toInt())

        repeat(count) { i ->
            val t = i / count.toFloat()
            wavyData += WavyData(
                point = Offset(from.x + dx * t, from.y + dy * t),
                normal = normal,
            )
        }
    }

    fun arc(center: Offset, radius: Float, start: Float, end: Float) {
        val count = max(1, (abs(end - start) * radius / spacing).toInt())

        repeat(count) { i ->
            val t = i / count.toFloat()
            val angle = start + (end - start) * t
            wavyData += WavyData(
                point = Offset(
                    center.x + cos(angle) * radius,
                    center.y + sin(angle) * radius,
                ),
                normal = Offset(cos(angle), sin(angle)),
            )
        }
    }

    line(Offset(rect.left + cornerRadius, rect.top), Offset(rect.right - cornerRadius, rect.top), Offset(0f, -1f))
    arc(Offset(rect.right - cornerRadius, rect.top + cornerRadius),
        cornerRadius, -PI.toFloat() / 2f, 0f)
    line(Offset(rect.right, rect.top + cornerRadius), Offset(rect.right, rect.bottom - cornerRadius), Offset(1f, 0f))
    arc(Offset(rect.right - cornerRadius, rect.bottom - cornerRadius),
        cornerRadius, 0f, PI.toFloat() / 2f)
    line(Offset(rect.right - cornerRadius, rect.bottom), Offset(rect.left + cornerRadius, rect.bottom), Offset(0f, 1f))
    arc(Offset(rect.left + cornerRadius, rect.bottom - cornerRadius),
        cornerRadius, PI.toFloat() / 2f, PI.toFloat())
    line(Offset(rect.left, rect.bottom - cornerRadius), Offset(rect.left, rect.top + cornerRadius), Offset(-1f, 0f))
    arc(Offset(rect.left + cornerRadius, rect.top + cornerRadius),
        cornerRadius, PI.toFloat(), PI.toFloat() * 1.5f)

    var nextSeed = seed
    val points = wavyData.map {
        nextSeed = nextSeed * 6364136223846793005L + 1442695040888963407L
        val random = ((nextSeed ushr 1) % 2000L) / 1000f - 1f
        val offset = random * amplitude
        it.point + it.normal * offset
    }

    return Path().apply {
        val start = midpoint(points.last(), points.first())
        moveTo(start.x, start.y)

        points.forEachIndexed { index, current ->
            val next = points[(index + 1) % points.size]
            val mid = midpoint(current, next)
            quadraticTo(current.x, current.y, mid.x, mid.y)
        }

        close()
    }
}

private fun midpoint(a: Offset, b: Offset): Offset =
    Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)

