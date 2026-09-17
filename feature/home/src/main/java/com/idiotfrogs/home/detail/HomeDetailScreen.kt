package com.idiotfrogs.home.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.util.LoadNextPageEffect
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.extension.toDdayCount
import com.idiotfrogs.extension.toYearMonthDay
import com.idiotfrogs.home.component.HomeSmallTicket
import com.idiotfrogs.navigation.HomeDetailType
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes.*
import com.idiotfrogs.resource.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeDetailRoute(
    homeDetailType: HomeDetailType,
    viewModel: HomeDetailViewModel =
        hiltViewModel<HomeDetailViewModel, HomeDetailViewModel.Factory>(
            key = homeDetailType.name
        ) { it.create(homeDetailType) },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect {
        when (it) {
            is HomeDetailSideEffect.NavigateToDetail -> navigator.navigate(Detail(it.id))
            HomeDetailSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            HomeDetailScreen(
                data = data,
                onAction = viewModel::onAction
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)
    }
}

@Composable
fun HomeDetailScreen(
    data: HomeDetailData,
    onAction: (HomeDetailAction) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()

    LoadNextPageEffect(
        scrollableState = lazyGridState,
        canLoadMore = data.detailItems.canLoadMore,
        onLoadNextPage = { onAction(HomeDetailAction.NextItemRequested) },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                modifier = Modifier
                    .noRippleClickable { onAction.invoke(HomeDetailAction.BackClicked) }
                    .size(24.dp),
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "ic_chevron_left"
            )
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            itemsIndexed(data.detailItems.items) { index, item ->
                val isLastRow = index / 2 == data.detailItems.items.lastIndex / 2
                HomeSmallTicket(
                    modifier = Modifier
                        .padding(
                            bottom = if (isLastRow) 0.dp else 16.dp
                        )
                        .noRippleClickable {
                            onAction.invoke(
                                HomeDetailAction.CapsuleClicked(item.timeCapsuleId)
                            )
                        },
                    dDayCount = item.openedAt?.toDdayCount(),
                    createdAt = item.createdAt.toYearMonthDay(),
                    title = item.title,
                    imageUrl = item.mainImageUrl,
                    step = item.stage
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeDetailScreenPreview() {
    HomeDetailScreen(
        data = HomeDetailData(),
        onAction = {}
    )
}