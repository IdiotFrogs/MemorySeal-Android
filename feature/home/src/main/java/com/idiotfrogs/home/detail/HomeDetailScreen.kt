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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.home.component.HomeSmallTicket
import com.idiotfrogs.navigation.HomeDetailType
import com.idiotfrogs.resource.R

@Composable
fun HomeDetailRoute(
    homeDetailType: HomeDetailType
) {
    HomeDetailScreen(homeDetailType)
}

@Composable
fun HomeDetailScreen(homeDetailType: HomeDetailType) {
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
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "ic_chevron_left"
            )
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
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

@Preview
@Composable
fun HomeDetailScreenPreview() {
    HomeDetailScreen(homeDetailType = HomeDetailType.BEFORE_BURIED)
}