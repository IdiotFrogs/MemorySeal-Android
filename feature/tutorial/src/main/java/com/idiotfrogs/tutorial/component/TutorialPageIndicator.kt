package com.idiotfrogs.tutorial.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme

/**
 * 하단 페이지 인디케이터. 현재 페이지는 초록색 바(20x6)로, 나머지는 회색 점(6x6)으로 표시한다.
 */
@Composable
fun TutorialPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            Spacer(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (isActive) 20.dp else 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isActive) MSTheme.color.primaryNormal else MSTheme.color.greyG2
                    )
            )
        }
    }
}

@Preview
@Composable
private fun TutorialPageIndicatorPreview() {
    TutorialPageIndicator(pageCount = 5, currentPage = 1)
}
