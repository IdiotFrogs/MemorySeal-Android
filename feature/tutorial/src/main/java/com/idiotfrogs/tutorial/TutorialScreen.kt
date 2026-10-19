package com.idiotfrogs.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.tutorial.component.TutorialPageIndicator
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun TutorialRoute(
    viewModel: TutorialViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            // 튜토리얼 종료 → 백스택을 비우고 홈(Empty)으로 랜딩한다.
            TutorialSideEffect.NavigateToHome -> navigator.resetToHome()
        }
    }

    TutorialScreen(
        onComplete = { viewModel.onAction(TutorialAction.Completed) },
    )
}

@Composable
fun TutorialScreen(
    onComplete: () -> Unit,
) {
    val pages = remember { TutorialPage.entries }
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            // 일러스트 + 문구는 페이지 단위로 스와이프 된다.
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                TutorialPageContent(page = pages[page])
            }

            // 인디케이터는 고정 위치에서 현재 페이지만 반영한다.
            TutorialPageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 464.dp),
            )

            // 건너뛰기: 1~4 페이지 어디서든 눌러 바로 종료할 수 있다. (마지막 페이지는 하단 버튼으로 종료)
            if (!isLastPage) {
                MSText(
                    text = "건너뛰기",
                    fontSize = 14.dp,
                    fontWeight = FontWeight.Medium,
                    color = MSTheme.color.greyG3,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 17.dp, end = 20.dp)
                        .noRippleClickable { onComplete() },
                )
            }

            MSButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(48.dp)
                    .wavyStroke(
                        color = MSTheme.color.primaryNormal,
                        drawType = DrawType.ALL,
                        clipContent = true,
                    ),
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            ) {
                MSText(
                    text = pages[pagerState.currentPage].buttonLabel,
                    fontSize = 16.dp,
                    lineHeight = 16.dp.toSp() * 1.6,
                    color = MSTheme.color.white,
                )
            }
        }
    }
}

@Composable
private fun TutorialPageContent(page: TutorialPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(72.dp))
        Image(
            painter = painterResource(page.illustration),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(360.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(70.dp))
        MSText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = page.title,
            fontSize = 22.dp,
            fontWeight = FontWeight.Bold,
            color = MSTheme.color.greyG5,
            lineHeight = 22.dp.toSp() * 1.4,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MSText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = page.body,
            fontSize = 16.dp,
            fontWeight = FontWeight.Normal,
            color = MSTheme.color.greyG4,
            lineHeight = 16.dp.toSp() * 1.5,
            textAlign = TextAlign.Center,
        )
    }
}

@DevicePreview
@Composable
private fun TutorialScreenPreview() {
    TutorialScreen(onComplete = {})
}
