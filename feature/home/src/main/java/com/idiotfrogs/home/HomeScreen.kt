package com.idiotfrogs.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.component.MSTabBar
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSToast
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.home.component.HomeHeader
import com.idiotfrogs.home.component.HomeJoinContainer
import com.idiotfrogs.home.component.HomeTicket
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.extension.toYearMonthDay
import com.idiotfrogs.home.component.OpenAnimation
import com.idiotfrogs.home.component.OpenInteraction
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.resource.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

enum class OpenStep { INTERACTION, ANIMATION, NONE }

@Composable
fun HomeRoute(
    openedId: Long?,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()
    var showToast by remember { mutableStateOf(false) }
    var currentOpenStep by remember { mutableStateOf(OpenStep.NONE) }
    var clickedCapsuleId by remember { mutableLongStateOf(-1L) }

    val visibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(openedId) {
        if (openedId != null) {
            viewModel.onAction(HomeAction.ShowOpenAnimation(openedId))
        }
    }

    LaunchedEffect(showToast) {
        if (!showToast) return@LaunchedEffect
        delay(2000L)
        showToast = false
    }

    viewModel.collectSideEffect {
        when (it) {
            HomeSideEffect.NavigateToCreate -> navigator.navigate(Routes.Create)
            HomeSideEffect.NavigateToProfile -> navigator.navigate(Routes.Profile)
            is HomeSideEffect.NavigateToDetail -> navigator.navigate(Routes.Detail(it.id))
            HomeSideEffect.ShowToast -> showToast = true
            is HomeSideEffect.ShowOpenAnimation -> {
                clickedCapsuleId = it.id
                currentOpenStep = OpenStep.INTERACTION
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            HomeScreen(
                showToast = showToast,
                data = data,
                isRefreshing = uiState.isLoading,
                onAction = viewModel::onAction
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)

        // 로티를 미리 로드해둬야 함
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ticket_open))
        val ready = composition != null

        // 만약 클릭한 캡슐이 있다면 파생해서 해당 캡슐 데이터를 얻음
        val clickedCapsule by remember {
            derivedStateOf {
                if (clickedCapsuleId == -1L) {
                    null
                } else {
                    uiState.data?.capsules?.flatMap { it.value }?.find { it.timeCapsuleId == clickedCapsuleId }
                }
            }
        }

        LaunchedEffect(currentOpenStep, ready) {
            if (currentOpenStep == OpenStep.ANIMATION && ready) {
                visibleState.targetState = true
            }
        }

        when (currentOpenStep) {
            OpenStep.INTERACTION -> {
                OpenInteraction(
                    image = clickedCapsule?.mainImageUrl,
                    onFinish = { currentOpenStep = OpenStep.ANIMATION}
                )
            }
            // 가드 조건(if) - 매칭된 조건에 대한 추가 검사 제공
            OpenStep.ANIMATION if ready -> {
                // 배경 지정 안하면 홈 화면이 비쳐보임
                Box(
                    modifier = Modifier
                        .noRippleClickable { /** no-op */ }
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(tween(500))
                    ) {
                        OpenAnimation(
                            image = clickedCapsule?.mainImageUrl,
                            composition = { composition!! }, // 위에서 체크해서 non-null, 람다를 통한 지연 읽기
                            confirmClick = {
                                // 로컬에 오픈 확인 이력 저장
                                viewModel.onAction(HomeAction.SeenTimeCapsule(clickedCapsuleId))
                                // id가 유효하지 않은 경우 해당 페이지 예외 발생 -> 뒤로 이동
                                navigator.navigate(Routes.Memory(clickedCapsuleId))
                                // 상태 초기화
                                clickedCapsuleId = -1L
                                currentOpenStep = OpenStep.NONE
                            }
                        )
                    }
                }
            }
            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    showToast: Boolean,
    data: HomeData,
    isRefreshing: Boolean,
    onAction: (HomeAction) -> Unit,
) {
    val hazeState = rememberHazeState()
    var expanded by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(HomeTab.CREATED) }
    var showJoinContainer by remember { mutableStateOf(false) }
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeHeight by remember { derivedStateOf { ime.getBottom(density) } }

    val showDim by remember {
        derivedStateOf { expanded || showJoinContainer }
    }

    val menuList by remember {
        mutableStateOf(
            listOf(
                MSMenuFabModel("새 티켓 생성하기") {
                    expanded = false
                    onAction(HomeAction.CreateClicked)
                },
                MSMenuFabModel("참여코드로 합류하기") {
                    expanded = false
                    showJoinContainer = true
                },
            )
        )
    }

    val textFieldState = rememberTextFieldState()

    LaunchedEffect(imeHeight) {
        if (showJoinContainer && imeHeight == 0) {
            showJoinContainer = false
            expanded = false
        }
    }

    val pagerState = rememberPagerState { HomeTab.entries.size }

    LaunchedEffect(currentTab) {
        pagerState.animateScrollToPage(
            page = currentTab.ordinal,
            animationSpec = tween()
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = HomeTab.entries[pagerState.currentPage]
    }

    Box {
        if (showToast) {
            MSToast(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.BottomCenter)
                    .systemBarsPadding()
                    .zIndex(1f),
                hazeState = hazeState,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_friend_accept),
                    contentDescription = "알림",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                MSText(
                    text = "타임 캡슐 참여 요청이 완료되었어요.",
                    color = MSTheme.color.white
                )
            }
        }

        val lazyListState = rememberLazyListState()
        val showBorder by remember {
            derivedStateOf {
                lazyListState.firstVisibleItemScrollOffset > 0 || // 1px이라도 움직였거나
                        lazyListState.firstVisibleItemIndex > 0   // 첫 번째 아이템을 넘어간 경우
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.bgNormal)
                .systemBarsPadding()
                .hazeSource(hazeState)
        ) {
            HomeHeader(
                profileUrl = data.user?.profileImageUrl,
                navigateToProfile = { onAction(HomeAction.ProfileClicked) }
            )
            MSTabBar(
                showBorder = showBorder,
                tabs = HomeTab.entries.map { it.title },
                selectedIndex = currentTab.ordinal,
                onClick = { currentTab = HomeTab.entries[it] },
            )
            if (data.capsules.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.Bottom)
                ) {
                    MSText(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(end = 15.dp),
                        text = "생성된 티켓이 없습니다\n버튼을 눌러서 티켓을 추가해 보세요",
                        fontSize = 14.dp,
                        fontWeight = FontWeight.Normal,
                        color = MSTheme.color.greyG4,
                        textAlign = TextAlign.Center
                    )
                    Image(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 107.dp)
                            .size(width = 92.dp, height = 246.dp),
                        painter = painterResource(R.drawable.img_home_empty),
                        contentDescription = "empty_home"
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                ) { page ->
                    val tab = HomeTab.entries[page]
                    val role = when (tab) {
                        HomeTab.CREATED -> TimeCapsuleRole.HOST
                        HomeTab.JOINED -> TimeCapsuleRole.CONTRIBUTOR
                    }
                    val data = data.capsules[role].orEmpty()
                    val refreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        state = refreshState,
                        onRefresh = { onAction.invoke(HomeAction.Refresh) }
                    ) {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 24.dp,
                                start = 20.dp,
                                end = 20.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(data) {
                                HomeTicket(
                                    modifier = Modifier.noRippleClickable {
                                        onAction(
                                            HomeAction.TimeCapsuleClicked(
                                                it.timeCapsuleId,
                                                it.timeCapsuleStatus
                                            )
                                        )
                                    },
                                    buried = it.timeCapsuleStatus == TimeCapsuleStatus.BURIED,
                                    createdAt = it.createdAt.toYearMonthDay(),
                                    title = it.title,
                                    imageUrl = it.mainImageUrl
                                )
                            }
                        }
                    }
                }
            }
        }
        MSDim(
            visible = showDim,
            onDismiss = {
                expanded = false
                showJoinContainer = false
            }
        )
        MSMenuFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 24.dp),
            expanded = expanded,
            hasFab = true,
            offset = DpOffset(x = 0.dp, y = (-16).dp),
            menuList = menuList,
            onClick = { expanded = !expanded },
            onDismiss = { expanded = false },
        )
        HomeJoinContainer(
            isShow = showJoinContainer,
            textFieldState = textFieldState,
            onJoin = { onAction(HomeAction.JoinCodeSubmitted(textFieldState.text.toString())) },
            onCancel = { showJoinContainer = false }
        )
    }
}

@DevicePreview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        showToast = false,
        data = HomeData(),
        isRefreshing = false,
        onAction = {},
    )
}

private enum class HomeTab(val title: String) {
    CREATED("생성한 티켓"),
    JOINED("참여한 티켓"),
}
