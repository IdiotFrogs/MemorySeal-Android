package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue

// 좌우 티켓 기울기
private const val SIDE_ROTATION = 15f

// 좌우 티켓 투명도
private const val SIDE_ALPHA = 0.12f

// 좌우 티켓을 중앙에 대비해서 아래로 내릴 크기
private val SIDE_OFFSET_Y = 22.dp

@Composable
fun HomeOpenedBanner(
    modifier: Modifier = Modifier,
    capsuleSteps: List<Int>,
) {
    // 단일 티켓인 경우 디자인이 다름
    if (capsuleSteps.size == 1) {
        HomeBigTicket(modifier = modifier, step = capsuleSteps.first())
        return
    }

    val pagerState = rememberPagerState { capsuleSteps.size }
    HorizontalPager(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp),
        state = pagerState,
        contentPadding = PaddingValues(start = 27.dp, end = 31.dp), // 티켓 바깥 여백
        pageSpacing = (-84).dp, // 음수 간격이기 때문에 각 페이지가 해당 값 만큼 포개진다.
        beyondViewportPageCount = 1, // 양 옆에도 보여야 하므로 해당 페이지 외 미리 로드
    ) { page ->
        HomeMiddleTicket(
            modifier = Modifier.deckPage(pagerState, page),
            step = capsuleSteps[page],
        )
    }
}

// 포개진 카드 덱 모양으로 변환하는 함수
// 중앙은 원본 색상이며 좌/우로 갈수록 최대 15도 기울며 0.12% 까지 옅어진다.
private fun Modifier.deckPage(state: PagerState, page: Int): Modifier {
    // 중앙에 있는 페이지 대비 거리 값
    val distanceFromCenter = (page - state.currentPage).absoluteValue

    return this
        .zIndex(-distanceFromCenter.toFloat()) // 중앙 (0)을 기준으로 음수 값
        .graphicsLayer {
            val offset = state.getOffsetDistanceInPages(page) // 중앙 기준 얼마나 떨어져 있는지 (소수점)
                .coerceIn(-1f, 1f)
            val distance = offset.absoluteValue

            rotationZ = SIDE_ROTATION * offset
            translationY = SIDE_OFFSET_Y.toPx() * distance
            alpha = lerp(1f, SIDE_ALPHA, distance)
        }
}

@Preview(showBackground = true, heightDp = 520)
@Composable
private fun HomeOpenedBannerPreview() {
    HomeOpenedBanner(capsuleSteps = listOf(2, 3, 4))
}