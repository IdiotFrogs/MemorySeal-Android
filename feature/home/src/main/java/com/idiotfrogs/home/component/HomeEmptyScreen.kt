package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@Composable
fun HomeEmptyScreen(
    modifier: Modifier = Modifier,
    selectedMenu: BottomMenu,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp, alignment = Alignment.Bottom)
    ) {
        Box(
            modifier = Modifier.paint(
                painter = painterResource(R.drawable.img_home_empty_guide),
                contentScale = ContentScale.Crop
            ),
            contentAlignment = Alignment.Center
        ) {
            MSText(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                text = (if (selectedMenu == BottomMenu.HOME) "생성한 티켓" else "오픈된 티켓") +
                        "이 없습니다\n버튼을 눌러서 티켓을 추가해 보세요",
                fontSize = 16.dp,
                fontWeight = FontWeight.Normal,
                color = MSTheme.color.greyG4,
                textAlign = TextAlign.Center
            )
        }
        Image(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 79.dp)
                .size(width = 102.dp, height = 239.dp),
            painter = painterResource(R.drawable.img_home_empty),
            contentDescription = "empty_home"
        )
        Spacer(modifier = Modifier.height(29.dp))
    }
}