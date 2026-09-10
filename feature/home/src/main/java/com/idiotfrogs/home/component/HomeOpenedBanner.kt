package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue

/** 인접한 두 티켓이 서로 겹치는 폭. */
private val TICKET_OVERLAP = 84.dp

/** 좌/우 이웃 티켓 기울기(도). 중앙은 0도. */
private const val SIDE_ROTATION = 15f

/** 좌/우 이웃 티켓 투명도. 중앙은 1f(원래 색). */
private const val SIDE_ALPHA = 0.12f

/** 좌/우 이웃 티켓을 아래로 내리는 정도. 중앙은 0. */
private val SIDE_OFFSET_Y = 22.dp

/**
 * 기울어진 모서리가 잘리지 않도록 페이지 위아래에 두는 여백.
 * 티켓을 축소하는 대신 이만큼 Pager가 커진다. 모서리가 잘려 보이면 이 값만 키우면 된다.
 */
private val TILT_MARGIN = 44.dp

/**
 * HomeMiddleTicket이 갖고 있던 바깥 여백(27/31)에 겹침 폭의 절반을 더한 값.
 * 페이지 슬롯이 그만큼 좁아져 슬롯 간격이 '티켓 폭 - TICKET_OVERLAP'이 된다.
 */
private val PAGER_PADDING = PaddingValues(
    start = 27.dp + TICKET_OVERLAP / 2,
    end = 31.dp + TICKET_OVERLAP / 2,
)

@Composable
fun HomeOpenedBanner(
    capsuleSteps: List<Int>,
) {
    if (capsuleSteps.size == 1) {
        HomeBigTicket(step = capsuleSteps.first())
        return
    }

    val pagerState = rememberPagerState { capsuleSteps.size }
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PAGER_PADDING,
        // 슬롯 밖으로 넘겨 그리는 만큼, 슬롯이 화면 밖인 페이지도 미리 구성해 가장자리 팝인을 막는다.
        beyondViewportPageCount = 1,
    ) { page ->
        HomeMiddleTicket(
            modifier = Modifier.deckPage(pagerState, page),
            step = capsuleSteps[page],
        )
    }
}

/**
 * 카드 덱처럼 보이도록 페이지에 적용하는 변환.
 * 중앙은 정면·원래 색이고 좌/우로 갈수록 [SIDE_ROTATION]만큼 기울며 [SIDE_ALPHA]까지 옅어진다.
 * 축소는 하지 않으므로 티켓이 그려지는 크기는 중앙·좌우 모두 같다.
 */
private fun Modifier.deckPage(state: PagerState, page: Int): Modifier {
    val distanceFromCenter = (page - state.currentPage).absoluteValue

    return this
        // 겹쳤을 때 중앙이 맨 위, 멀어질수록 아래로. 페이지끼리 정렬되려면 체인 맨 앞이어야 한다.
        // 매 프레임 변하는 offsetFraction이 아니라 currentPage만 읽어 재구성을 최소화한다.
        .zIndex(-distanceFromCenter.toFloat())
        // 아래로 내린 이웃 티켓까지 담기도록 하단 여백만 SIDE_OFFSET_Y 더 준다.
        .padding(top = TILT_MARGIN, bottom = TILT_MARGIN + SIDE_OFFSET_Y)
        // 슬롯보다 TICKET_OVERLAP만큼 넓게 측정해 좌우로 균등하게 넘겨 그린다.
        // 부모에 보고하는 폭은 슬롯 그대로라 Pager의 페이지 크기는 변하지 않고 티켓끼리만 겹친다.
        .layout { measurable, constraints ->
            val overlap = TICKET_OVERLAP.roundToPx()
            val width = constraints.maxWidth + overlap
            val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
            layout(constraints.maxWidth, placeable.height) {
                placeable.place(-overlap / 2, 0)
            }
        }
        .graphicsLayer {
            val offset = state.deckOffsetOf(page)
            val distance = offset.absoluteValue

            rotationZ = SIDE_ROTATION * offset
            translationY = SIDE_OFFSET_Y.toPx() * distance
            // 겹친 wavyStroke·가이드 이미지가 따로 진해지지 않도록 레이어 단위로 알파를 준다.
            alpha = lerp(1f, SIDE_ALPHA, distance)
        }
}

/** -1 = 왼쪽 이웃, 0 = 중앙, +1 = 오른쪽 이웃. 드래그 중에도 연속적인 값이다. */
private fun PagerState.deckOffsetOf(page: Int): Float =
    ((page - currentPage) - currentPageOffsetFraction).coerceIn(-1f, 1f)

@Preview(showBackground = true, heightDp = 520)
@Composable
private fun HomeOpenedBannerPreview() {
    HomeOpenedBanner(capsuleSteps = listOf(2, 3, 4))
}