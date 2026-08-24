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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import com.idiotfrogs.model.timecapsule.WateringMeta
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
    val watering = viewModel.watering.collectAsLazyPagingItems()

    val navigator = LocalComposeMSNavigator.current
    viewModel.collectSideEffect { event ->
        when (event) {
            WateringDetailSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        state.data?.let { data ->
            WateringDetailScreen(
                watering = watering,
                data = data,
                onAction = viewModel::onAction
            )
        }

        MSLoadingOverlay(
            visible = watering.loadState.refresh is LoadState.Loading || state.isLoading
        )
    }
}
@Composable
fun WateringDetailScreen(
    watering: LazyPagingItems<WateringContentResponse>,
    data: WateringMeta,
    onAction: (WateringDetailAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
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
                progress = { data.wateringCount / data.totalDays.toFloat() },
                color = MSTheme.color.primaryNormal,
                trackColor = MSTheme.color.primaryLight,
                strokeCap = StrokeCap.Round,
                gapSize = (-18).dp, // 내부적으로 높이만큼 떨어지는 것 보정
                drawStopIndicator = { } // 끝 점 제거
            )
            Spacer(modifier = Modifier.height(40.dp))
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = 20.dp),
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(
                    watering.itemCount,
                    key = watering.itemKey { it.wateredDate.toString() }
                ) { index ->
                    val item = watering[index]
                    val highlightItem = index == 0
                    item?.let { item ->
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .wavyStroke(
                                    strokeWidth = 4.dp,
                                    color = if (item.isWatered) {
                                        MSTheme.color.primaryNormal
                                    } else {
                                        MSTheme.color.greyG1
                                    },
                                    cornerRadius = 54.dp, // 원형
                                    amplitude = 1.dp,
                                    spacing = 2.dp,
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
        MSButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .height(48.dp),
            onClick = { onAction.invoke(WateringDetailAction.WateringClicked) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.black,
                disabledContainerColor =  MSTheme.color.greyG3
            ),
            pressColors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.black,
                disabledContainerColor =  MSTheme.color.greyG3
            ),
            wavyStrokeColor = if (true) MSTheme.color.black else MSTheme.color.greyG3,
        ) {
            MSText(
                text = if (true) "물주기" else "물주기 완료",
                color = if (true) MSTheme.color.white else MSTheme.color.greyG2,
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
        watering = flowOf(PagingData.empty<WateringContentResponse>())
            .collectAsLazyPagingItems(),
        data = WateringMeta(
            totalDays = 20,
            wateringCount = 1,
            stage = 1
        ),
        onAction = { },
    )
}