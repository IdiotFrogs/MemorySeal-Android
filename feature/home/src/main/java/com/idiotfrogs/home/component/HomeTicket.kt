package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeTicket(
    countdown: String,
    targetDate: String,
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .zIndex(1f)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    strokeWidth = 4.dp,
                    fillColor = MSTheme.color.primaryNormal
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    MSText(
                        text = countdown,
                        fontSize = 32.dp,
                    )
                    MSText(
                        text = title,
                        fontSize = 16.dp,
                    )
                }
                MSText(
                    text = targetDate,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.dp,
                    color = MSTheme.color.greyG4
                )
            }
        }
        val mask = ImageBitmap.imageResource(id = R.drawable.img_mask_main)
        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    strokeWidth = 4.dp,
                    fillColor = MSTheme.color.white
                )
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            imageUrl?.let {
                GlideImage(
                    modifier = Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .fillMaxSize()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(8.dp))
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
                    imageModel = { imageUrl }
                )
            } ?: run {
                Image(
                    modifier = Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .fillMaxSize()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(8.dp))
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
                    painter = painterResource(R.drawable.img_sample),
                    contentDescription = "thumbnail",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTicketPreview() {
    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        HomeTicket(
            countdown = "D-5",
            targetDate = "2027. 10. 24.",
            title = "제목입니다.",
            imageUrl = null
        )
    }
}