package com.idiotfrogs.home.component

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable

fun LazyGridScope.maxLineItem(
    content: @Composable (LazyGridItemScope.() -> Unit),
) {
    this.item(
        span = { GridItemSpan(maxLineSpan) },
        content = content
    )
}