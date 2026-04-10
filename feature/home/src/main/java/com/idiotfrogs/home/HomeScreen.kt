package com.idiotfrogs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.home.component.HomeHeader
import com.idiotfrogs.home.component.HomeJoinContainer
import com.idiotfrogs.home.component.HomeTab
import com.idiotfrogs.home.component.HomeTabBar
import com.idiotfrogs.home.component.HomeTicket
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.util.UiState
import com.idiotfrogs.extension.toDday
import com.idiotfrogs.extension.toYearMonthDay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect {
        when (it) {
            HomeSideEffect.NavigateToCreate -> navigator.navigate(Routes.Create)
            HomeSideEffect.NavigateToProfile -> navigator.navigate(Routes.Profile)
            is HomeSideEffect.NavigateToDetail -> navigator.navigate(Routes.Detail(it.id))
        }
    }

    when (val state = uiState) {
        UiState.Init -> {}
        is UiState.Success -> {
            HomeScreen(
                data = state.data,
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> {}
    }
}

@Composable
fun HomeScreen(
    data: HomeData,
    onAction: (HomeAction) -> Unit,
) {
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
                    onAction(HomeAction.NavigateToCreate)
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
        pagerState.animateScrollToPage(currentTab.ordinal)
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = HomeTab.entries[pagerState.currentPage]
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MSTheme.color.bgNormal)
                .systemBarsPadding()
        ) {
            HomeHeader(
                profileUrl = data.user?.profileImageUrl,
                navigateToProfile = { onAction(HomeAction.NavigateToProfile) }
            )
            HomeTabBar(
                selectedTab = currentTab,
                onClick = { currentTab = it },
            )
            HorizontalPager(
                state = pagerState,
            ) { page ->
                val tab = HomeTab.entries[page]
                val role = when (tab) {
                    HomeTab.CREATED -> TimeCapsuleRole.HOST
                    HomeTab.JOINED -> TimeCapsuleRole.CONTRIBUTOR
                }
                val data = data.capsules[role].orEmpty()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        top = 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(data) {
                        HomeTicket(
                            modifier = Modifier.noRippleClickable {
                                onAction(HomeAction.NavigateToDetail(it.timeCapsuleId))
                            },
                            countdown = it.openedAt.toDday(),
                            targetDate = it.openedAt.toYearMonthDay(),
                            title = it.title,
                            imageUrl = it.mainImageUrl
                        )
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
            onJoin = { /** TODO: 타임 티켓 참여 */ },
            onCancel = { showJoinContainer = false }
        )
    }
}

@DevicePreview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        data = HomeData(),
        onAction = {},
    )
}