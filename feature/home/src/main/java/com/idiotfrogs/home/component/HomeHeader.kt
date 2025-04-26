package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.R
import com.idiotfrogs.resource.pretendard

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .background(color = MSTheme.color.white)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "타임 티켓",
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 24.dp.toSp(),
            color = MSTheme.color.greyG5
        )
        Spacer(modifier = Modifier.weight(1f))
        // TODO: 이미지 url 통해 로드
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.img_profile),
            contentDescription = "profile"
        )
    }
}

@Preview
@Composable
private fun HomeHeaderPreview() {
    HomeHeader()
}