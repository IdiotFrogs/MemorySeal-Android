package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

enum class GuideItem(val imgRes: Int, val width: Dp, val height: Dp) {
    STEP_2(imgRes = R.drawable.img_ticket_guide_step2, width = 395.dp, height = 359.dp),
    STEP_3(imgRes = R.drawable.img_ticket_guide_step3, width = 394.dp, height = 363.dp),
    STEP_4(imgRes = R.drawable.img_ticket_guide_step4, width = 383.dp, height = 354.dp),
    STEP_5(imgRes = R.drawable.img_ticket_guide_step5, width = 383.dp, height = 446.dp)
}

@Composable
fun HomeTicket(
    modifier: Modifier = Modifier,
    buried: Boolean,
//    createdAt: String,
//    title: String,
//    imageUrl: String?,
//    step: Int,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .zIndex(1f)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.primaryNormal,
                    amplitude = (1.5).dp,
                    spacing = 4.dp,
                )
                .fillMaxWidth()
                .height(55.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (buried) {
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .background(
                            color = MSTheme.color.primaryLight.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(start = 6.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_shovels),
                        contentDescription = "ic_shovels"
                    )
                    MSText(
                        text = "D-12",
                        fontSize = 12.dp,
                        fontWeight = FontWeight.SemiBold,
                        color = MSTheme.color.greyG5
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.white,
                    amplitude = (1.5).dp,
                    spacing = 4.dp,
                )
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val mask = ImageBitmap.imageResource(id = R.drawable.img_mask_main)
            GlideImage(
                modifier = Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .fillMaxSize()
                    .padding(12.dp)
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
                imageModel = { R.drawable.img_sample }
            )
        }
        Spacer(modifier = Modifier.height(2.dp)) // 위에서 offset 준 만큼 원본에서 차감
        MSText(
            text = "제목입니다. 제목입니다.",
            fontWeight = FontWeight.Bold,
            fontSize = 16.dp,
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.height(8.dp))
        MSText(
            text = "2027. 10. 24 ~ 2027. 10. 24 ",
            fontWeight = FontWeight.Normal,
            fontSize = 12.dp,
            color = MSTheme.color.greyG5.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTicketPreview() {
    HomeTicket(
        buried = true,
//        createdAt = "2027. 10. 24.",
//        title = "제목입니다.",
//        imageUrl = null,
//        step = 1
    )
}