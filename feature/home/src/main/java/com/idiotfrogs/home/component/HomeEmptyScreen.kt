package com.idiotfrogs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
    ) {
        Spacer(modifier = Modifier.weight(1f)) // 아래 컴포넌트가 중앙 정렬되어야 하므로 미리 영역을 차지한다
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .offset(y = (-56).dp), // 디자인 스펙 상 정확히 중앙이 아님
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .paint(
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
            // 먼저 정렬한 다음 남은 영역 최대치 높이를 기준으로 width를 설정한다
            Image(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 79.dp)
                    .fillMaxHeight(),
                painter = painterResource(R.drawable.img_home_empty),
                contentDescription = "empty_home",
                contentScale = ContentScale.FillHeight
            )
            Spacer(modifier = Modifier.height(29.dp))
        }
    }
}