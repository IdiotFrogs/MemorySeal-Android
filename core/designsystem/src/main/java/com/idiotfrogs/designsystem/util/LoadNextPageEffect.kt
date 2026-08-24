package com.idiotfrogs.designsystem.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LoadNextPageEffect(
    listState: LazyListState,
    canLoadMore: Boolean,
    onLoadNextPage: () -> Unit,
) {
    val currentCanLoadMore by rememberUpdatedState(canLoadMore)
    val currentOnLoadNextPage by rememberUpdatedState(onLoadNextPage)

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            layoutInfo.visibleItemsInfo.lastOrNull()?.index to layoutInfo.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (lastVisibleIndex, totalItemsCount) ->
                val hasReachedEnd = lastVisibleIndex != null &&
                    totalItemsCount > 0 &&
                    lastVisibleIndex >= totalItemsCount - 1

                if (currentCanLoadMore && hasReachedEnd) {
                    currentOnLoadNextPage()
                }
            }
    }
}
