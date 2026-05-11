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

enum class DrawType { TOP, BOTTOM, START, END, ALL }

fun Modifier.wavyStroke(
    color: Color,
    drawType: DrawType = DrawType.ALL,
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

            val path = when (drawType) {
                DrawType.TOP -> makeTopWavyPath(
                    width = size.width,
                    spacing = spacingPx,
                    amplitude = ampPx,
                    seed = seed,
                    strokeWidth = strokePx,
                )
                DrawType.BOTTOM -> makeBottomWavyPath(
                    width = size.width,
                    height = size.height,
                    spacing = spacingPx,
                    amplitude = ampPx,
                    seed = seed,
                    strokeWidth = strokePx,
                )
                DrawType.START -> makeStartWavyPath(
                    height = size.height,
                    spacing = spacingPx,
                    amplitude = ampPx,
                    seed = seed,
                    strokeWidth = strokePx,
                )
                DrawType.END -> makeEndWavyPath(
                    width = size.width,
                    height = size.height,
                    spacing = spacingPx,
                    amplitude = ampPx,
                    seed = seed,
                    strokeWidth = strokePx,
                )
                DrawType.ALL -> {
                    makeWavyPath(
                        rect = rect,
                        cornerRadius = radius,
                        spacing = spacingPx,
                        amplitude = ampPx,
                        seed = seed + size.width.roundToInt() + size.height.roundToInt() * 1_000_003L,
                    )
                }
            }

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

fun Modifier.wavyBackground(
    color: Color,
    drawType: DrawType = DrawType.ALL,
    strokeWidth: Dp = 3.dp,
    cornerRadius: Dp = 12.dp,
    amplitude: Dp = 2.dp,
    spacing: Dp = 6.dp,
    seed: Long = 0xC0FFEE_BABEL,
    fillColor: Color = color,
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

            val paths = when (drawType) {
                DrawType.ALL -> {
                    val path = makeWavyPath(
                        rect = rect,
                        cornerRadius = radius,
                        spacing = spacingPx,
                        amplitude = ampPx,
                        seed = seed,
                    )

                    WavyBackgroundPath(
                        fillPath = path,
                        strokePath = path,
                    )
                }

                DrawType.TOP,
                DrawType.BOTTOM,
                DrawType.START,
                DrawType.END -> makeEdgeWavyBackgroundPath(
                    width = size.width,
                    height = size.height,
                    edge = drawType,
                    spacing = spacingPx,
                    amplitude = ampPx,
                    seed = seed,
                    strokeWidth = strokePx,
                )
            }

            onDrawWithContent {
                drawPath(
                    path = paths.fillPath,
                    color = fillColor,
                )

                if (clipContent) {
                    clipPath(paths.fillPath) {
                        this@onDrawWithContent.drawContent()
                    }
                } else {
                    drawContent()
                }

                drawPath(
                    path = paths.strokePath,
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

private data class WavyBackgroundPath(val fillPath: Path, val strokePath: Path)

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

private fun makeTopWavyPath(
    width: Float,
    spacing: Float,
    amplitude: Float,
    seed: Long,
    strokeWidth: Float,
): Path {
    val yBase = strokeWidth / 2f
    val count = max(1, (width / spacing).toInt())

    return makeSingleAxisWavyPath(
        count = count,
        amplitude = amplitude,
        seed = seed,
    ) { t, offset ->
        Offset(
            x = width * t,
            y = yBase + offset,
        )
    }
}

private fun makeBottomWavyPath(
    width: Float,
    height: Float,
    spacing: Float,
    amplitude: Float,
    seed: Long,
    strokeWidth: Float,
): Path {
    val yBase = height - strokeWidth / 2f
    val count = max(1, (width / spacing).toInt())

    return makeSingleAxisWavyPath(
        count = count,
        amplitude = amplitude,
        seed = seed,
    ) { t, offset ->
        Offset(
            x = width * t,
            y = yBase + offset,
        )
    }
}

private fun makeStartWavyPath(
    height: Float,
    spacing: Float,
    amplitude: Float,
    seed: Long,
    strokeWidth: Float,
): Path {
    val xBase = strokeWidth / 2f
    val count = max(1, (height / spacing).toInt())

    return makeSingleAxisWavyPath(
        count = count,
        amplitude = amplitude,
        seed = seed,
    ) { t, offset ->
        Offset(
            x = xBase + offset,
            y = height * t,
        )
    }
}

private fun makeEndWavyPath(
    width: Float,
    height: Float,
    spacing: Float,
    amplitude: Float,
    seed: Long,
    strokeWidth: Float,
): Path {
    val xBase = width - strokeWidth / 2f
    val count = max(1, (height / spacing).toInt())

    return makeSingleAxisWavyPath(
        count = count,
        amplitude = amplitude,
        seed = seed,
    ) { t, offset ->
        Offset(
            x = xBase + offset,
            y = height * t,
        )
    }
}

/** 단일 Axis로 WavyPath를 그리는 공통함수 */
private fun makeSingleAxisWavyPath(
    count: Int,
    amplitude: Float,
    seed: Long,
    pointAt: (t: Float, offset: Float) -> Offset,
): Path {
    var nextSeed = seed

    val points = buildList {
        repeat(count + 1) { i ->
            val t = i / count.toFloat()

            nextSeed = nextSeed * 6364136223846793005L + 1442695040888963407L
            val random = ((nextSeed ushr 1) % 2000L) / 1000f - 1f
            val offset = random * amplitude

            add(pointAt(t, offset))
        }
    }

    return Path().apply {
        if (points.isEmpty()) return@apply

        val start = midpoint(points.first(), points.getOrElse(1) { points.first() })
        moveTo(start.x, start.y)

        points.zipWithNext { current, next ->
            val mid = midpoint(current, next)
            quadraticTo(current.x, current.y, mid.x, mid.y)
        }

        lineTo(points.last().x, points.last().y)
    }
}

private fun midpoint(a: Offset, b: Offset): Offset =
    Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)

private fun makeEdgeWavyBackgroundPath(
    width: Float,
    height: Float,
    edge: DrawType,
    spacing: Float,
    amplitude: Float,
    seed: Long,
    strokeWidth: Float,
): WavyBackgroundPath {
    val inset = amplitude + strokeWidth / 2f

    val count = when (edge) {
        DrawType.TOP,
        DrawType.BOTTOM -> max(1, (width / spacing).toInt())

        DrawType.START,
        DrawType.END -> max(1, (height / spacing).toInt())

        DrawType.ALL -> 1
    }

    var nextSeed = seed

    val points = buildList {
        repeat(count + 1) { i ->
            val t = i / count.toFloat()

            nextSeed = nextSeed * 6364136223846793005L + 1442695040888963407L
            val random = ((nextSeed ushr 1) % 2000L) / 1000f - 1f
            val offset = random * amplitude

            add(
                when (edge) {
                    DrawType.TOP -> Offset(
                        x = width * t,
                        y = inset + offset,
                    )

                    DrawType.BOTTOM -> Offset(
                        x = width * t,
                        y = height - inset + offset,
                    )

                    DrawType.START -> Offset(
                        x = inset + offset,
                        y = height * t,
                    )

                    DrawType.END -> Offset(
                        x = width - inset + offset,
                        y = height * t,
                    )

                    DrawType.ALL -> Offset.Zero
                }
            )
        }
    }

    val strokePath = makeOpenSmoothPath(points)

    val fillPath = Path().apply {
        val first = points.first()
        moveTo(first.x, first.y)

        points.zipWithNext { current, next ->
            val mid = midpoint(current, next)
            quadraticTo(current.x, current.y, mid.x, mid.y)
        }

        val last = points.last()
        lineTo(last.x, last.y)

        when (edge) {
            DrawType.TOP -> {
                lineTo(width, height)
                lineTo(0f, height)
            }

            DrawType.BOTTOM -> {
                lineTo(0f, 0f)
                lineTo(width, 0f)
            }

            DrawType.START -> {
                lineTo(width, height)
                lineTo(width, 0f)
            }

            DrawType.END -> {
                lineTo(0f, height)
                lineTo(0f, 0f)
            }

            DrawType.ALL -> Unit
        }

        close()
    }

    return WavyBackgroundPath(
        fillPath = fillPath,
        strokePath = strokePath,
    )
}

private fun makeOpenSmoothPath(points: List<Offset>): Path {
    return Path().apply {
        if (points.isEmpty()) return@apply

        val first = points.first()
        moveTo(first.x, first.y)

        points.zipWithNext { current, next ->
            val mid = midpoint(current, next)
            quadraticTo(current.x, current.y, mid.x, mid.y)
        }

        val last = points.last()
        lineTo(last.x, last.y)
    }
}