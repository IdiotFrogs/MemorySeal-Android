package com.idiotfrogs.watering

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.idiotfrogs.watering.WateringDetailViewModel.Companion.WATERING_DETAIL_LOAD_SIZE
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.math.min
import kotlin.time.Clock

@Composable
fun WateringDetailRoute(
    capsuleId: Long,
    viewModel: WateringDetailViewModel =
        hiltViewModel<WateringDetailViewModel, WateringDetailViewModel.Factory>(
            key = capsuleId.toString()
        ) {
            it.create(capsuleId)
        },
) {
    val state by viewModel.collectAsState()
    val navigator = LocalComposeMSNavigator.current

    viewModel.collectSideEffect { event ->
        when (event) {
            WateringDetailSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        state.data?.let { data ->
            WateringDetailScreen(
                data = data,
                onAction = viewModel::onAction
            )
        }
        MSLoadingOverlay(visible = state.data != null && state.isLoading)
    }
}
@Composable
fun WateringDetailScreen(
    data: WateringDetailData,
    onAction: (WateringDetailAction) -> Unit
) {
    val gridState = rememberLazyGridState()

    // 로드된 오른쪽 끝 index (asc라 낮은 index부터 채워지고, 전부 로드되면 totalElements - 1)
    val loadedMax = min(
        (data.waterings.currentPage + 1) * WATERING_DETAIL_LOAD_SIZE,
        data.waterings.totalElements.toInt()
    ) - 1

    // 아래로 스크롤 해 로드 경계에 근접하면 다음 페이지 요청하여 빈 셀을 채움
    LaunchedEffect(gridState, data.waterings) {
        snapshotFlow {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= loadedMax - 5 // 미리 당겨올 값 조정
                    && data.waterings.canLoadMore
                    && !data.waterings.isLoadingMore
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onAction.invoke(WateringDetailAction.NextWateringRequested) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MSDetailHeader(
                title = "물주기",
                fontSize = 20.dp,
                navigateToBack = { onAction.invoke(WateringDetailAction.BackClicked) }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_watering_can),
                    contentDescription = "ic_watering_can"
                )
                Spacer(modifier = Modifier.width(6.dp))
                MSAnnotatedText(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(color = MSTheme.color.primaryNormal),
                        ) {
                            append(data.wateringCount.toString())
                        }
                        withStyle(
                            style = SpanStyle(color = MSTheme.color.greyG5),
                        ) {
                            append(" / ${data.totalDays}일")
                        }
                    },
                    fontSize = 14.dp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(11.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(18.dp),
                progress = {
                    data.totalDays.takeIf { it != null && it != 0L }
                        ?.let { totalDays -> (data.wateringCount ?: 0) / totalDays.toFloat() }
                        ?: 0f
                },
                color = MSTheme.color.primaryNormal,
                trackColor = MSTheme.color.primaryLight,
                strokeCap = StrokeCap.Round,
                gapSize = (-18).dp, // 내부적으로 높이만큼 떨어지는 것 보정
                drawStopIndicator = { } // 끝 점 제거
            )
            Spacer(modifier = Modifier.height(40.dp))
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = 20.dp),
                state = gridState,
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(
                    data.waterings.items,
                    key = { _, item -> item.wateredDate.toString() }
                ) { index, item ->
                    val highlightItem = item.wateredDate == Clock.System.todayIn(TimeZone.currentSystemDefault())
                    if (item.wateredDate < today && !item.isWatered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .wavyStroke(
                                    strokeWidth = 4.dp,
                                    color = MSTheme.color.greyG1,
                                    cornerRadius = 48.dp,
                                    amplitude = 1.dp,
                                    spacing = 2.dp,
                                    fillColor = MSTheme.color.greyG1
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_xmark),
                                colorFilter = ColorFilter.tint(MSTheme.color.greyG3),
                                contentDescription = "not_watered"
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .wavyStroke(
                                    strokeWidth = 4.dp,
                                    color = if (item.isWatered) {
                                        MSTheme.color.primaryNormal
                                    } else {
                                        MSTheme.color.greyG1
                                    },
                                    cornerRadius = 54.dp, // 원형
                                    amplitude = 1.dp,
                                    spacing = 2.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            MSText(
                                text = if (highlightItem) "오늘" else (index + 1).toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.dp,
                                color = MSTheme.color.black
                            )
                            GlideImage(
                                modifier = Modifier
                                    .size(38.dp)
                                    .wavyStroke(
                                        strokeWidth = 4.dp,
                                        color = if (item.isWatered) {
                                            MSTheme.color.primaryNormal
                                        } else {
                                            MSTheme.color.greyG1
                                        },
                                        cornerRadius = 38.dp, // 원형
                                        amplitude = 1.dp,
                                        spacing = 2.dp,
                                        clipContent = true
                                    ),
                                imageModel = { item.profileImageUrl }
                            )
                        }
                    }
                }
            }
        }
        val todayWatered = data.waterings.items.find { it.wateredDate == today }?.isWatered ?: false
        MSButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .height(48.dp),
            enabled = !todayWatered,
            onClick = { onAction.invoke(WateringDetailAction.WateringClicked) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.black,
                disabledContainerColor =  MSTheme.color.greyG3
            ),
            pressColors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.black,
                disabledContainerColor =  MSTheme.color.greyG3
            ),
            wavyStrokeColor = if (!todayWatered) MSTheme.color.black else MSTheme.color.greyG3,
        ) {
            MSText(
                text = if (!todayWatered) "물주기" else "물주기 완료",
                color = if (!todayWatered) MSTheme.color.white else MSTheme.color.greyG2,
                fontWeight = FontWeight.Bold,
                fontSize = 16.dp
            )
        }
    }
}

@Preview
@Composable
private fun WateringDetailScreenPreview() {
    WateringDetailScreen(
        data = WateringDetailData(),
        onAction = { },
    )
}