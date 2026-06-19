package com.idiotfrogs.create

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@Composable
fun CreateLoadingScreen(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition()
    val coverProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
            .consumePointerInput()
            .semantics {
                contentDescription = "Creating ticket"
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(160.dp))

        MSText(
            text = "티켓을 만드는중이에요\n잠시만 기다려주세요",
            fontSize = 14.dp,
            fontWeight = FontWeight.Bold,
            color = MSTheme.color.black,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(38.dp))

        Image(
            painter = painterResource(R.drawable.img_create_loading_ticket),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(
                    width = 220.dp,
                    height = 278.dp,
                )
                .drawWithContent {
                    drawContent()

                    val coverWidth = size.width * coverProgress
                    val featherWidth = 24.dp.toPx()

                    if (coverWidth > 0f) {
                        drawRect(
                            color = Color.White,
                            size = Size(
                                width = coverWidth,
                                height = size.height,
                            ),
                        )

                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.White, Color.Transparent),
                                startX = coverWidth,
                                endX = coverWidth + featherWidth,
                            ),
                            topLeft = Offset(
                                x = coverWidth,
                                y = 0f,
                            ),
                            size = Size(
                                width = featherWidth,
                                height = size.height,
                            ),
                        )
                    }
                },
        )
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

@Preview(showBackground = true)
@Composable
private fun CreateLoadingScreenPreview() {
    MSTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CreateLoadingScreen()
        }
    }
}
