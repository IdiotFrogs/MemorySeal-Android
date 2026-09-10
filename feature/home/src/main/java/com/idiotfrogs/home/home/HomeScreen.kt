package com.idiotfrogs.home.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DevicePreview
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.home.component.HomeHeader
import com.idiotfrogs.home.component.HomeJoinContainer
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.home.component.BottomMenu
import com.idiotfrogs.home.component.HomeBigTicket
import com.idiotfrogs.home.component.HomeBottomBar
import com.idiotfrogs.home.component.HomeEmptyScreen
import com.idiotfrogs.home.component.HomeOpenedBanner
import com.idiotfrogs.home.component.HomeRemindBanner
import com.idiotfrogs.home.component.HomeSectionDivider
import com.idiotfrogs.home.component.HomeSmallTicket
import com.idiotfrogs.home.component.OpenedTicket
import com.idiotfrogs.home.component.Weather
import com.idiotfrogs.home.component.maxLineItem
import com.idiotfrogs.navigation.HomeDetailType
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val TOP_BAR_SIZE = 56.dp
private val BOTTOM_BAR_SIZE = 80.dp

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
            is HomeSideEffect.NavigateToHomeDetail -> navigator.navigate(Routes.HomeDetail(it.homeDetailType))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            HomeScreen(
                data = data,
                isRefreshing = uiState.isLoading,
                onAction = viewModel::onAction
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    data: HomeData,
    isRefreshing: Boolean,
    onAction: (HomeAction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showJoinContainer by remember { mutableStateOf(false) }

    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeHeight by remember { derivedStateOf { ime.getBottom(density) } }
    val showDim by remember { derivedStateOf { expanded || showJoinContainer } }

    val menuList by remember {
        mutableStateOf(
            listOf(
                MSMenuFabModel("새 티켓 생성하기") {
                    expanded = false
                    onAction.invoke(HomeAction.CreateClicked)
                },
                MSMenuFabModel("참여코드로 합류하기") {
                    expanded = false
                    showJoinContainer = true
                },
            )
        )
    }

    val textFieldState = rememberTextFieldState()
    val pagerState = rememberPagerState(initialPage = 0) { BottomMenu.entries.size }

    LaunchedEffect(imeHeight) {
        if (showJoinContainer && imeHeight == 0) {
            showJoinContainer = false
            expanded = false
        }
    }

    var selectedMenu by remember { mutableStateOf(BottomMenu.HOME) }

    LaunchedEffect(selectedMenu) {
        pagerState.animateScrollToPage(selectedMenu.ordinal)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding()
    ) {
        HomeHeader(
            selectedMenu = selectedMenu,
            profileUrl = data.user?.profileImageUrl,
            navigateToProfile = { onAction.invoke(HomeAction.ProfileClicked) }
        )
        HorizontalPager(
            modifier = Modifier.padding(top = TOP_BAR_SIZE),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                BottomMenu.HOME.ordinal -> {
                    if (false) {
                        HomeEmptyScreen(
                            modifier = Modifier.padding(bottom = BOTTOM_BAR_SIZE),
                            selectedMenu = BottomMenu.HOME
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy((-12).dp) // 줄기가 겹쳐지도록
                        ) {
                            if (true) {
                                maxLineItem {
                                    HomeOpenedBanner(
                                        capsuleSteps = listOf(1,2,3,4)
                                    )
                                }
                            }
                            if (true) { // todo: 조건 변경
                                maxLineItem {
                                    HomeRemindBanner(
                                        modifier = Modifier.padding(20.dp),
                                        weather = Weather.AUTUMN
                                    )
                                }
                            }
                            maxLineItem {
                                HomeSectionDivider(
                                    modifier = Modifier
                                        .noRippleClickable {
                                            onAction.invoke(
                                                HomeAction.HomeDetailClicked(
                                                    HomeDetailType.BEFORE_BURIED
                                                )
                                            )
                                        }
                                        .padding(horizontal = (22.5).dp, vertical = 20.dp),
                                    sectionName = "타임 티켓"
                                )
                            }
                            itemsIndexed(listOf(1, 1, 1, 1, 1, 1)) { index, item ->
                                val isLastRow = index / 2 == 2 // 추후 하드코딩에서 변경
                                HomeSmallTicket(
                                    modifier = Modifier.padding(
                                        bottom = if (isLastRow) 0.dp else 16.dp
                                    ),
                                    buried = index / 2 == 0,
                                    step = index
                                )
                            }
                            maxLineItem {
                                HomeSectionDivider(
                                    modifier = Modifier
                                        .noRippleClickable {
                                            onAction.invoke(
                                                HomeAction.HomeDetailClicked(
                                                    HomeDetailType.BURIED
                                                )
                                            )
                                        }
                                        .padding(horizontal = (22.5).dp)
                                        .padding(top = 40.dp, bottom = 15.dp),
                                    sectionName = "오픈 예정 티켓"
                                )
                            }
                            itemsIndexed(listOf(1, 1, 1, 1, 1, 1)) { index, item ->
                                val isLastRow = index / 2 == 2 // 추후 하드코딩에서 변경
                                HomeSmallTicket(
                                    modifier = Modifier.padding(
                                        bottom = if (isLastRow) 0.dp else 16.dp
                                    ),
                                    buried = index / 2 == 0,
                                    step = index
                                )
                            }
                        }
                    }
                }
                BottomMenu.OPENED.ordinal -> {
                    if (false) {
                        HomeEmptyScreen(
                            modifier = Modifier.padding(bottom = BOTTOM_BAR_SIZE),
                            selectedMenu = BottomMenu.OPENED
                        )
                    } else {
                        LazyVerticalGrid(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            contentPadding = PaddingValues(top = 20.dp, bottom = BOTTOM_BAR_SIZE)
                        ) {
                            items(10) {
                                OpenedTicket()
                            }
                        }
                    }
                }
            }
        }

        fun onDimClick() {
            expanded = false; showJoinContainer = false
        }

        MSDim(
            modifier = Modifier.padding(bottom = 80.dp), // 하단바 영역 침범 x
            visible = showDim,
            onDismiss = { onDimClick() }
        )
        HomeBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedMenu = selectedMenu,
            showDim = showDim,
            fabMenuList = menuList,
            expanded = expanded,
            onSelectChange = { selectedMenu = it },
            onExpandChange = { expanded = it },
            onClickDim = { onDimClick() }
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
        data = HomeData(),
        isRefreshing = false,
        onAction = {},
    )
}