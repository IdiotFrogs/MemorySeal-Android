package com.idiotfrogs.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.pretendard

@Composable
fun HomeTicket(
    countdown: String,
    targetDate: String,
    title: String,
) {
    Column {
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
            Text(
                text = countdown,
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 32.dp.toSp(),
                color = MSTheme.color.black
            )
            Text(
                text = targetDate,
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.dp.toSp(),
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
                .offset(y = (-2).dp) // FIXME: DashedDivider margin
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color(0xBFA6A6A6),
                    spotColor = Color(0xBFA6A6A6)
                )
                .fillMaxWidth()
                .height(348.dp)
                .background(
                    color = MSTheme.color.white,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Text(
                text = title,
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.dp.toSp(),
                color = MSTheme.color.black
            )
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: 이미지 url 통해 로드
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.img_sample),
                contentDescription = "thumbnail",
                contentScale = ContentScale.Crop
            )
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
    ) {
        val dashPx = with(density) { dashDp.toPx() }
        val strokePx = with(density) { strokeDp.toPx() }

        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
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
            title = "제목입니다."
        )
    }
}