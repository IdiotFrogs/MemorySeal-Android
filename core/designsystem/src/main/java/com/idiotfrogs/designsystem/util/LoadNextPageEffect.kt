package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

data class PagingItem(
    val index: Int,
    val key: Any,
    val totalItemsCount: Int
)

@Composable
fun LoadNextPageEffect(
    scrollableState: ScrollableState,
    canLoadMore: Boolean,
    expectedLastItemKey: String? = null,
    onLoadNextPage: () -> Unit,
) {
    val currentCanLoadMore by rememberUpdatedState(canLoadMore)
    val currentExpectedLastItemKey by rememberUpdatedState(expectedLastItemKey)
    val currentOnLoadNextPage by rememberUpdatedState(onLoadNextPage)

    LaunchedEffect(scrollableState) {
        snapshotFlow {
            val pagingItem = scrollableState.lastPagingItem ?: return@snapshotFlow false
            // 위에서 null 체크를 했으므로 이후 null check가 필요 없음
            val hasReachedEnd = if (currentExpectedLastItemKey == null) {
                pagingItem.totalItemsCount > 0 &&
                pagingItem.index >= pagingItem.totalItemsCount - 1
            } else {
                pagingItem.key == currentExpectedLastItemKey && !scrollableState.canScrollForward
            }

            currentCanLoadMore && hasReachedEnd
        }
            .distinctUntilChanged()
            .filter { hasReachedEnd -> hasReachedEnd }
            .collect { currentOnLoadNextPage() }
    }
}

@Composable
fun LoadPrevPageEffect(
    lazyListState: LazyListState,
    loadedMinIndex: Int,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    scrolledToday: Boolean,
    onLoadPrevPage: () -> Unit
) {
    val currentLoadedMinIndex by rememberUpdatedState(loadedMinIndex)
    val currentCanLoadMore by rememberUpdatedState(canLoadMore)
    val currentIsLoadingMore by rememberUpdatedState(isLoadingMore)
    val currentScrolledToday by rememberUpdatedState(scrolledToday)
    val currentOnLoadPrevPage by rememberUpdatedState(onLoadPrevPage)

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex <= currentLoadedMinIndex + 5 // 미리 당겨올 값 조정
                    && currentCanLoadMore && !currentIsLoadingMore
                    && currentScrolledToday // 없으면 계속해서 index가 0이므로 페이지를 연속해서 호출
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { currentOnLoadPrevPage() }
    }
}

private val ScrollableState.lastPagingItem: PagingItem?
    get() = when (this) {
        is LazyListState -> this.layoutInfo.let {
            it.visibleItemsInfo.lastOrNull()?.let { lastVisibleItemInfo ->
                PagingItem(lastVisibleItemInfo.index, lastVisibleItemInfo.key, it.totalItemsCount)
            }
        }
        is LazyGridState -> this.layoutInfo.let {
            it.visibleItemsInfo.lastOrNull()?.let { lastVisibleItemInfo ->
                PagingItem(lastVisibleItemInfo.index, lastVisibleItemInfo.key, it.totalItemsCount)
            }
        }
        else -> null
    }
