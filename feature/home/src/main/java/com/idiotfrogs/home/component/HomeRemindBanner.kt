package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.R

enum class Weather(val text: String) {
    SPRING("봄"), SUMMER("여름"), AUTUMN("가을"), WINTER("겨울")
}

@Composable
fun HomeRemindBanner(
    modifier: Modifier = Modifier,
    weather: Weather
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(213.dp)
            .wavyStroke(
                strokeWidth = 4.dp,
                cornerRadius = 27.dp,
                color = MSTheme.color.greyG5,
                fillColor = MSTheme.color.bgNormal,
                clipContent = true
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MSText(
                    text = "지난 ${weather.text} 우리",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.dp,
                    color = MSTheme.color.greyG5
                )
                MSText(
                    text = "티켓이름티켓이름티켓이름",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5
                )
            }
            Box(
                modifier = Modifier
                    .size(53.dp)
                    .wavyStroke(
                        color = MSTheme.color.black,
                        clipContent = true
                    )
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.img_sample),
                    contentScale = ContentScale.Crop,
                    contentDescription = "img_ticket"
                )
            }
        }
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            painter = painterResource(R.drawable.img_remind_banner_bg),
            contentScale = ContentScale.FillBounds,
            contentDescription = "img_remind_banner_bg"
        )
    }
}

@Preview
@Composable
private fun HomeRemindBannerPreview() {
    HomeRemindBanner(weather = Weather.AUTUMN)
}