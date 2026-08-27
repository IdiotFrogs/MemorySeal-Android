package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun LoadNextPageEffect(
    listState: LazyListState,
    canLoadMore: Boolean,
    expectedLastItemKey: Any? = null,
    onLoadNextPage: () -> Unit,
) {
    val currentCanLoadMore by rememberUpdatedState(canLoadMore)
    val currentExpectedLastItemKey by rememberUpdatedState(expectedLastItemKey)
    val currentOnLoadNextPage by rememberUpdatedState(onLoadNextPage)

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val hasReachedEnd = if (currentExpectedLastItemKey == null) {
                lastVisibleItem != null &&
                    layoutInfo.totalItemsCount > 0 &&
                    lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
            } else { lastVisibleItem?.key == currentExpectedLastItemKey && !listState.canScrollForward }

            currentCanLoadMore && hasReachedEnd
        }
            .distinctUntilChanged()
            .filter { hasReachedEnd -> hasReachedEnd }
            .collect { currentOnLoadNextPage() }
    }
}
