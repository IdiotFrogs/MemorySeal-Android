package com.idiotfrogs.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

private val TicketWidth = 269.dp
private val HandleSize = 50.dp

private val LottieWidth = 308.dp
private val LottieHeight = 165.dp

@Composable
fun OpenInteraction(
    image: String?,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .noRippleClickable(onClick = onFinish)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFD2D3D6),
                    1f to Color(0xFFF5F5F6)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(84.dp))
        MSText(
            text = "티켓을 열어서\n추억을 확인해보세요!",
            fontWeight = FontWeight.Bold,
            fontSize = 24.dp,
            color = MSTheme.color.black,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.zIndex(1f)) {
                // handle을 앞으로 보내려면 외부 Box가 아닌 내부에서 그려야 함
                Box(
                    modifier = Modifier
                        .size(width = TicketWidth, height = 64.dp)
                        .wavyStroke(
                            color = MSTheme.color.black,
                            cornerRadius = 16.dp,
                            strokeWidth = 4.dp,
                            amplitude = 1.dp,
                            spacing = 3.dp,
                            fillColor = MSTheme.color.white
                        )
                )
                Image(
                    painter = painterResource(R.drawable.ic_handle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = HandleSize / 2, y = HandleSize / 2)
                        .size(HandleSize)
                )
            }
            Box(
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .size(TicketWidth)
                    .wavyStroke(
                        color = MSTheme.color.black,
                        cornerRadius = 16.dp,
                        strokeWidth = 4.dp,
                        amplitude = (1.5).dp,
                        spacing = 4.dp,
                        fillColor = MSTheme.color.white
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG1,
                            cornerRadius = 16.dp,
                            strokeWidth = 4.dp,
                            amplitude = (1.5).dp,
                            spacing = 4.dp,
                            fillColor = MSTheme.color.greyG1
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (image?.takeIf { it.isNotEmpty() } != null) {
                        val mask = ImageBitmap.imageResource(id = R.drawable.img_mask_main)
                        GlideImage(
                            modifier = Modifier
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                .size(width = 80.dp, height = 102.dp)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawImage(
                                            image = mask,
                                            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                                            blendMode = BlendMode.DstIn
                                        )
                                    }
                                },
                            imageModel = { image },
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(width = 80.dp, height = 102.dp),
                            painter = painterResource(R.drawable.img_empty_placeholder),
                            contentDescription = "empty_placeholder"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OpenAnimation(
    image: String?,
    composition: () -> LottieComposition,
    confirmClick: () -> Unit
) {
    val progress by animateLottieCompositionAsState(
        composition = composition(),
        iterations = 1,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(R.drawable.img_ticket_open_background),
                    contentScale = ContentScale.Crop
                )
                .padding(top = 104.dp), // 피그마 기준 + 티켓 아래 부분이 28dp 더 위로 올라간거 보정
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로티 아트보드(308x165) 안의 티켓이 269x64 이므로, 아트보드 크기를 그대로 지정해야 티켓이 의도한 269x64 로 그려진다.
            LottieAnimation(
                modifier = Modifier
                    .zIndex(1f)
                    .size(width = LottieWidth, height = LottieHeight),
                composition = composition(),
                progress = { progress }
            )
            Box(
                modifier = Modifier
                    .offset(y = (-38).dp)
                    .size(TicketWidth)
                    .wavyStroke(
                        color = MSTheme.color.black,
                        cornerRadius = 16.dp,
                        strokeWidth = 4.dp,
                        amplitude = (1.5).dp,
                        spacing = 4.dp,
                        fillColor = MSTheme.color.white
                    )
            ) {
                val mask = ImageBitmap.imageResource(id = R.drawable.img_mask_main)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG1,
                            cornerRadius = 16.dp,
                            strokeWidth = 4.dp,
                            amplitude = (1.5).dp,
                            spacing = 4.dp,
                            fillColor = MSTheme.color.greyG1
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (image?.takeIf { it.isNotEmpty() } != null) {
                        val mask = ImageBitmap.imageResource(id = R.drawable.img_mask_main)
                        GlideImage(
                            modifier = Modifier
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                .size(width = 80.dp, height = 102.dp)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawImage(
                                            image = mask,
                                            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                                            blendMode = BlendMode.DstIn
                                        )
                                    }
                                },
                            imageModel = { image },
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(width = 80.dp, height = 102.dp),
                            painter = painterResource(R.drawable.img_empty_placeholder),
                            contentDescription = "empty_placeholder"
                        )
                    }
                }
            }
        }
        MSButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
                .fillMaxWidth()
                .height(48.dp),
            wavyStrokeColor = MSTheme.color.primaryNormal,
            onClick = confirmClick
        ) {
            MSText(
                text = "추억 메시지 확인",
                fontSize = 16.dp,
                color = MSTheme.color.white,
                fontWeight = FontWeight.Bold
            )
        }
    }
}