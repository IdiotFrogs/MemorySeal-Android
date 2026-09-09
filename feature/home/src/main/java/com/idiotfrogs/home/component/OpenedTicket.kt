package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun OpenedTicket() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .rotate(10f)
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.white
                )
                .fillMaxWidth()
                .height(40.dp)
        )
        Box(
            modifier = Modifier
                .wavyStroke(
                    color = MSTheme.color.greyG5,
                    fillColor = MSTheme.color.white
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
        Spacer(modifier = Modifier.height(12.dp))
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

@Preview(widthDp = 160, heightDp = 286)
@Composable
fun OpenedTicketPreview() {
    OpenedTicket()
}