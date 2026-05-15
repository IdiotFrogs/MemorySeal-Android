package com.idiotfrogs.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSTabBar
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.message.component.MessagePreviewBanner
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MessageRoute(
    capsuleId: Long,
    viewModel: MessageViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            MessageSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            MessageScreen(
                capsuleId = capsuleId,
                onAction = viewModel::onAction,
            )
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun MessageScreen(
    capsuleId: Long,
    onAction: (MessageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentTab by remember { mutableStateOf(MessageTab.MESSAGE) }
    val pagerState = rememberPagerState { MessageTab.entries.size }

    LaunchedEffect(currentTab) {
        pagerState.animateScrollToPage(currentTab.ordinal)
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = MessageTab.entries[pagerState.currentPage]
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MSTheme.color.bgNormal),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Column(
                modifier = Modifier.background(MSTheme.color.white),
            ) {
                MSDetailHeader(
                    title = "나의 추억 메시지",
                    navigateToBack = { onAction(MessageAction.NavigateToBack) },
                    paddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_trashcan),
                        contentDescription = "삭제",
                    )
                }
                MSTabBar(
                    tabs = MessageTab.entries.map { it.title },
                    selectedIndex = currentTab.ordinal,
                    onClick = { currentTab = MessageTab.entries[it] },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val tab = MessageTab.entries[page]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    MessagePreviewBanner(
                        imageRes = tab.bannerImageRes,
                        title = tab.bannerTitle,
                        description = tab.bannerDescription,
                        onPreviewClick = {
                            // TODO 미리보기 화면이 추가되면 연결
                        },
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 24.dp)
                .wavyStroke(
                    color = Color(0xFF29A047),
                    strokeWidth = 4.dp,
                    cornerRadius = 28.dp,
                    amplitude = 1.dp,
                    spacing = 2.dp,
                )
                .size(56.dp)
                .padding(2.dp),
            shape = CircleShape,
            containerColor = MSTheme.color.primaryNormal,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 4.dp,
                focusedElevation = 4.dp,
                hoveredElevation = 4.dp,
            ),
            onClick = {
                // TODO 메시지 또는 사진 추가 액션 연결
            },
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "추억 메시지 추가",
            )
        }
    }
}

private enum class MessageTab(
    val title: String,
    val bannerImageRes: Int,
    val bannerTitle: String,
    val bannerDescription: String,
) {
    MESSAGE(
        title = "메시지",
        bannerImageRes = R.drawable.img_message_banner,
        bannerTitle = "메시지",
        bannerDescription = "메시지로 추억을 남겨보세요.",
    ),
    PHOTO(
        title = "사진",
        bannerImageRes = R.drawable.img_photo_banner,
        bannerTitle = "사진",
        bannerDescription = "사진으로 추억을 남겨보세요.",
    ),
}

@Preview
@Composable
fun MessageScreenPreview() {
    MessageScreen(
        capsuleId = 0L,
        onAction = {},
    )
}
