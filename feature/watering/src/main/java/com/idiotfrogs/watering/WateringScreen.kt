package com.idiotfrogs.watering

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.LoadPrevPageEffect
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.idiotfrogs.watering.WateringViewModel.Companion.WATERING_LOAD_SIZE
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.math.min
import kotlin.time.Clock

@Composable
fun WateringRoute(
    capsuleId: Long,
    viewModel: WateringViewModel =
        hiltViewModel<WateringViewModel, WateringViewModel.Factory>(
            key = capsuleId.toString()
        ) {
            it.create(capsuleId)
        },
) {
    val state by viewModel.collectAsState()
    val navigator = LocalComposeMSNavigator.current

    viewModel.collectSideEffect { event ->
        when (event) {
            WateringSideEffect.NavigateToBack -> navigator.popBackStack()
            is WateringSideEffect.NavigateToWateringDetail -> {
                navigator.navigate(Routes.WateringDetail(event.id))
            }
        }
    }

    val lazyListState = rememberLazyListState()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // 오늘 날짜 스크롤 여부
    var scrolledToday by remember { mutableStateOf(false) }

    LaunchedEffect(state.data) {
        if (!scrolledToday && state.data != null) {
            val todayIndex = state.data!!.waterings.items.indexOfFirst { it.wateredDate == today }
            if (todayIndex != -1) {
                lazyListState.scrollToItem(todayIndex)
                scrolledToday = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        state.data?.let { data ->
            WateringScreen(
                lazyListState = lazyListState,
                scrolledToday = scrolledToday,
                data = data,
                onAction = viewModel::onAction
            )
        }
        MSLoadingOverlay(visible = state.data != null && state.isLoading)
    }
}

enum class WateringItem(val imgRes: Int, val width: Dp, val height: Dp) {
    STEP_1(imgRes = R.drawable.img_watering_step1, width = 94.dp, height = 88.dp),
    STEP_2(imgRes = R.drawable.img_watering_step2, width = 112.dp, height = 206.dp),
    STEP_3(imgRes = R.drawable.img_watering_step3, width = 112.dp, height = 206.dp),
    STEP_4(imgRes = R.drawable.img_watering_step4, width = 136.dp, height = 247.dp),
    STEP_5(imgRes = R.drawable.img_watering_step5, width = 136.dp, height = 247.dp)
}


@Composable
fun WateringScreen(
    lazyListState: LazyListState,
    scrolledToday: Boolean,
    data: WateringData,
    onAction: (WateringAction) -> Unit,
) {
    val currentStage = data.stage?.minus(1)?.coerceAtLeast(0) ?: 0
    val wateringItem = WateringItem.entries[currentStage]

    // (현재 페이지 수 * 한 번 로드된 크기) = 예상 로드 레코드 수
    val loadedRecord = (data.waterings.currentPage + 1) * WATERING_LOAD_SIZE
    // 예상 레코드 수와 실제 레코드 최대 값을 비교해 작은 쪽으로 판정
    val adjustLoadedRecord = min(loadedRecord, data.waterings.totalElements.toInt())
    // 로드된 구간의 왼쪽 끝 (전부 로드되면 0)
    val loadedMinIndex = data.waterings.totalElements.toInt() - adjustLoadedRecord

    LoadPrevPageEffect(
        lazyListState = lazyListState,
        loadedMinIndex = loadedMinIndex,
        canLoadMore = data.waterings.canLoadMore,
        isLoadingMore = !data.waterings.isLoadingMore,
        scrolledToday = scrolledToday,
        onLoadPrevPage = { onAction.invoke(WateringAction.NextWateringRequested) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MSDetailHeader(
            title = "물주기",
            fontSize = 20.dp,
            navigateToBack = { onAction.invoke(WateringAction.BackClicked)}
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(
                width = wateringItem.width,
                height = wateringItem.height
            ),
            painter = painterResource(wateringItem.imgRes),
            contentDescription = "watering"
        )
        Spacer(modifier = Modifier.height(24.dp))
        MSText(
            text = "티켓에 물을 주다보면\n메실 티켓이 자라나요!",
            fontWeight = FontWeight.Bold,
            fontSize = 16.dp,
            color = MSTheme.color.greyG5,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        MSText(
            text = "티켓은 누구나 매일\n물을 주기만해도돼요.",
            fontWeight = FontWeight.Normal,
            fontSize = 12.dp,
            color = MSTheme.color.greyG3,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(60.dp))
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
            Spacer(modifier = Modifier.weight(1f))
            MSText(
                modifier = Modifier.noRippleClickable(
                    onClick = { onAction.invoke(WateringAction.ShowAllClicked)}
                ),
                text = "전체보기",
                fontWeight = FontWeight.Medium,
                fontSize = 12.dp,
                color = MSTheme.color.greyG3
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
        Spacer(modifier = Modifier.height(32.dp))
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(
                data.waterings.items,
                key = { _, item -> item.wateredDate.toString() }
            ) { index, item ->
                val highlightItem = item.wateredDate == today
                if (item.wateredDate < today && !item.isWatered) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                            .size(
                                if (highlightItem) 64.dp else 48.dp
                            )
                            .wavyStroke(
                                strokeWidth = 4.dp,
                                color = if (item.isWatered) {
                                    MSTheme.color.primaryNormal
                                } else {
                                    MSTheme.color.greyG1
                                },
                                cornerRadius = if (highlightItem) 64.dp else 48.dp, // 원형
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
                                .size(
                                    if (highlightItem) 48.dp else 32.dp
                                )
                                .wavyStroke(
                                    strokeWidth = 4.dp,
                                    color = if (item.isWatered) {
                                        MSTheme.color.primaryNormal
                                    } else {
                                        MSTheme.color.greyG1
                                    },
                                    cornerRadius = if (highlightItem) 48.dp else 32.dp, // 원형
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
        Spacer(modifier = Modifier.height(32.dp))
        val todayWatered = data.waterings.items.find { it.wateredDate == today }?.isWatered ?: false
        MSButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 20.dp),
            enabled = !todayWatered,
            onClick = { onAction.invoke(WateringAction.WateringClicked) },
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
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun WateringScreenPreview() {
    WateringScreen(
        lazyListState = rememberLazyListState(),
        scrolledToday = true,
        data = WateringData(),
        onAction = { },
    )
}