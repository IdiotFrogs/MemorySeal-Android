package com.idiotfrogs.home.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
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
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color(0xBFA6A6A6),
                    spotColor = Color(0xBFA6A6A6)
                )
                .fillMaxWidth()
                .background(
                    color = MSTheme.color.white,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MSText(
                text = countdown,
                fontSize = 32.dp,
            )
            MSText(
                text = targetDate,
                fontWeight = FontWeight.Normal,
                fontSize = 14.dp,
                color = MSTheme.color.greyG4
            )
        }
        DashedDivider(
            modifier = Modifier
                .zIndex(1f)
                .padding(horizontal = 16.dp)
        )
        Column(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color(0xBFA6A6A6),
                    spotColor = Color(0xBFA6A6A6)
                )
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    color = MSTheme.color.white,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            MSText(
                text = title,
                fontSize = 16.dp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            imageUrl?.let {
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    imageModel = { imageUrl }
                )
            } ?: run {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.img_sample),
                    contentDescription = "thumbnail",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = MSTheme.color.greyG2,
    dashDp: Dp = 8.dp,
    strokeDp: Dp = 2.dp,
) {
    val density = LocalDensity.current

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeDp)
            .background(MSTheme.color.white)
    ) {
        val dashPx = with(density) { dashDp.toPx() }
        val strokePx = with(density) { strokeDp.toPx() }

        drawLine(
            color = color,
            start = Offset(0f, strokePx / 2),
            end = Offset(size.width, strokePx / 2),
            strokeWidth = strokePx,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashPx, dashPx),
                phase = dashPx / 2f
            )
        )
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
            imageUrl = ""
        )
    }
}