package com.idiotfrogs.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSCalender
import com.idiotfrogs.designsystem.component.MSDashHorizontalDivider
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSToast
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DrawType
import com.idiotfrogs.designsystem.util.WavyAlign
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.extension.toOpenRemainingText
import com.idiotfrogs.extension.toYearMonthDayWeek
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.skydoves.landscapist.glide.GlideImage
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun DetailRoute(
    capsuleId: Long,
    viewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(key = capsuleId.toString()) { it.create(capsuleId) },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()
    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(showToast) {
        if (!showToast) return@LaunchedEffect
        delay(2000L)
        showToast = false
    }

    viewModel.collectSideEffect { event ->
        when (event) {
            DetailSideEffect.NavigateToBack -> navigator.popBackStack()
            is DetailSideEffect.NavigateToFriend -> navigator.navigate(Routes.Friend(event.id))
            is DetailSideEffect.NavigateToMessage -> navigator.navigate(Routes.Message(event.id))
            is DetailSideEffect.NavigateToPreview -> navigator.navigate(Routes.Preview(event.id))
            is DetailSideEffect.NavigateToManagement -> navigator.navigate(
                Routes.Management(
                    id = event.id,
                    title = event.title,
                )
            )
            DetailSideEffect.ShowToast -> showToast = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            DetailScreen(
                data = data,
                capsuleId = capsuleId,
                showToast = showToast,
                onAction = viewModel::onAction,
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun DetailScreen(
    data: TimeCapsuleData,
    capsuleId: Long,
    showToast: Boolean,
    onAction: (DetailAction) -> Unit,
) {
    val hazeState = rememberHazeState()
    val scrollState = rememberScrollState()
    val capsule = data.capsule
    val status = capsule?.timeCapsuleStatus ?: TimeCapsuleStatus.BEFOREBURIED
    val role = capsule?.userRole ?: TimeCapsuleRole.CONTRIBUTOR
    val isHost = role == TimeCapsuleRole.HOST
    val isBuried = status == TimeCapsuleStatus.BURIED
    var showBuryDialog by remember { mutableStateOf(false) }

    val defaultOpenAt = remember {
        Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .plus(1, DateTimeUnit.DAY)
            .atTime(0, 0, 0, 0)
    }
    var selectedOpenAt by remember { mutableStateOf(defaultOpenAt) }

    if (showBuryDialog) {
        MSTitleDialog(
            title = "티켓 오픈일 설정",
            confirmText = "묻기",
            cancelText = "취소",
            onConfirm = {
                onAction(DetailAction.BuryConfirmClicked(selectedOpenAt))
                showBuryDialog = false
            },
            onCancel = { showBuryDialog = false },
        ) {
            Spacer(Modifier.height(8.dp))
            MSText(
                text = "한번 묻은 티켓은 오픈일까지 다시 오픈,\n수정 할 수 없습니다.",
                fontSize = 14.dp,
                fontWeight = FontWeight.Normal,
                color = MSTheme.color.greyG3,
            )
            Spacer(Modifier.height(32.dp))
            MSCalender(showSealDate = true) { selectedOpenAt = it }
            Spacer(Modifier.height(32.dp))
        }
    }

    Box {
        if (showToast) {
            MSToast(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .zIndex(1f),
                hazeState = hazeState,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_friend_reject),
                    contentDescription = "알림",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                MSText(
                    text = "티켓 묻기에 실패했어요.",
                    color = MSTheme.color.white,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .navigationBarsPadding()
                .background(Color.White)
                .verticalScroll(scrollState),
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .wavyStroke(
                    color = Color.Black,
                    drawType = DrawType.BOTTOM,
                    strokeWidth = 5.dp,
                    amplitude = 2.dp,
                    spacing = 3.dp,
                    clipContent = true,
                    wavyAlign = WavyAlign.OUTER
                )
        ) {
            capsule?.mainImageUrl?.let {
                GlideImage(
                    modifier = Modifier.matchParentSize(),
                    imageModel = { it },
                )
            } ?: Image(
                modifier = Modifier.matchParentSize(),
                painter = painterResource(R.drawable.img_sample),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop
            )
            Image(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 20.dp)
                    .size(32.dp)
                    .noRippleClickable { onAction(DetailAction.BackClicked) },
                painter = painterResource(R.drawable.btn_detail_back),
                contentDescription = "Close"
            )
            Image(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp)
                    .size(32.dp)
                    .noRippleClickable {
                        onAction(
                            DetailAction.ManagementClicked(
                                id = capsuleId,
                                title = capsule?.title.orEmpty(),
                            )
                        )
                    },
                painter = painterResource(R.drawable.btn_detail_management),
                contentDescription = "Management"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            if (isBuried) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MSTheme.color.greyG1)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_shovel),
                        contentDescription = "ic_shovel",
                    )

                    Spacer(Modifier.width(4.dp))

                    MSText(
                        text = capsule?.openedAt.toOpenRemainingText(),
                        fontSize = 12.dp,
                        color = MSTheme.color.greyG4,
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            if (status == TimeCapsuleStatus.OPENED) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wavyStroke(
                            color = MSTheme.color.primaryLight,
                            fillColor = MSTheme.color.primaryLight,
                            contentPadding = 16.dp,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MSText(
                        modifier = Modifier.weight(1f),
                        text = "타임캡슐이 열렸어요!\n함께 쌓인 추억을 보러가요.",
                        fontSize = 14.dp,
                        color = MSTheme.color.primaryDark,
                    )

                    Spacer(Modifier.width(12.dp))

                    MSButton(
                        onClick = { onAction(DetailAction.PreviewClicked(capsuleId)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MSTheme.color.greyG5,
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
                    ) {
                        MSText(
                            text = "보러가기",
                            fontSize = 12.dp,
                            color = MSTheme.color.white,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            MSText(
                text = capsule?.title.orEmpty(),
                fontSize = 20.dp,
                color = MSTheme.color.greyG5,
            )

            Spacer(Modifier.height(12.dp))

            MSText(
                text = capsule?.description.orEmpty(),
                fontSize = 16.dp,
                fontWeight = FontWeight.Normal,
                color = MSTheme.color.greyG5,
            )

            Spacer(Modifier.height(12.dp))

            MSText(
                text = if (status == TimeCapsuleStatus.BEFOREBURIED) {
                    capsule?.createdAt.toYearMonthDayWeek() + " ~ 오픈일"
                } else {
                    capsule?.createdAt.toYearMonthDayWeek() + " ~ " + capsule?.openedAt.toYearMonthDayWeek()
                },
                fontSize = 14.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG3,
            )

            when (status) {
                TimeCapsuleStatus.BEFOREBURIED -> {
                    if (isHost) {
                        Spacer(Modifier.height(28.dp))
                        MSDashHorizontalDivider(
                            thickness = 2.dp,
                            dashWidth = 8.dp,
                            gapWidth = 8.dp,
                            color = MSTheme.color.greyG1,
                        )
                        Spacer(Modifier.height(28.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MSText(
                                text = "티켓 묻기",
                                fontSize = 14.dp,
                                color = MSTheme.color.greyG5,
                            )
                            MSButton(
                                onClick = { showBuryDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG5,
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                MSText(
                                    text = "티켓 묻기",
                                    fontSize = 12.dp,
                                    color = MSTheme.color.white,
                                )
                            }
                        }
                        Spacer(Modifier.height(28.dp))
                        MSDashHorizontalDivider(
                            thickness = 2.dp,
                            dashWidth = 8.dp,
                            gapWidth = 8.dp,
                            color = MSTheme.color.greyG1,
                        )
                    } else {
                        Spacer(Modifier.height(28.dp))
                        MSDashHorizontalDivider(
                            thickness = 2.dp,
                            dashWidth = 8.dp,
                            gapWidth = 8.dp,
                            color = MSTheme.color.greyG1,
                        )
                    }
                }

                TimeCapsuleStatus.BURIED -> {
                    Spacer(Modifier.height(28.dp))
                    MSDashHorizontalDivider(
                        thickness = 2.dp,
                        dashWidth = 8.dp,
                        gapWidth = 8.dp,
                        color = MSTheme.color.greyG1,
                    )
                    Spacer(Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    ) {
                        Image(
                            modifier = Modifier.matchParentSize(),
                            painter = painterResource(R.drawable.bg_detail_watering),
                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
                        )
                        Row(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(horizontal = 20.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MSText(
                                modifier = Modifier.weight(1f),
                                text = "티켓이 열릴때까지 매실 나무에\n 물을 주면 보상을 드려요.",
                                fontSize = 16.dp,
                                color = MSTheme.color.greyG5,
                            )
                            Spacer(Modifier.width(12.dp))
                            MSButton(
                                onClick = {
                                    // TODO 물주기 화면 또는 액션이 정해지면 연결
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG5,
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
                            ) {
                                MSText(
                                    text = "물주러가기",
                                    fontSize = 12.dp,
                                    color = MSTheme.color.white,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                    MSDashHorizontalDivider(
                        thickness = 2.dp,
                        dashWidth = 8.dp,
                        gapWidth = 8.dp,
                        color = MSTheme.color.greyG1,
                    )
                }

                TimeCapsuleStatus.OPENED -> Unit
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MSText(
                    text = "나의 추억 메시지",
                    color = MSTheme.color.greyG4,
                )
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickable { onAction(DetailAction.MessageSectionClicked(capsuleId)) },
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wavyStroke(
                            color = MSTheme.color.primaryLight,
                            fillColor = MSTheme.color.primaryLight,
                            contentPadding = 16.dp,
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    MSText(
                        text = "메시지",
                        fontSize = 12.dp,
                        color = MSTheme.color.primaryDark,
                    )
                    Spacer(Modifier.height(4.dp))
                    MSText(
                        text = "${capsule?.myContentCount}개 등록",
                        fontSize = 12.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.primaryDark,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wavyStroke(
                            color = MSTheme.color.primaryLight,
                            fillColor = MSTheme.color.primaryLight,
                            contentPadding = 16.dp,
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    MSText(
                        text = "사진",
                        fontSize = 12.dp,
                        color = MSTheme.color.primaryDark,
                    )
                    Spacer(Modifier.height(4.dp))
                    MSText(
                        text = "${capsule?.myImageCount}개 등록",
                        fontSize = 12.dp,
                        fontWeight = FontWeight.Medium,
                        color = MSTheme.color.primaryDark,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MSAnnotatedText(
                    text = buildAnnotatedString {
                        append("멤버 ")
                        withStyle(
                            SpanStyle(color = MSTheme.color.primaryNormal)
                        ) { append(data.collaborators?.content?.size.toString()) }
                    },
                    color = MSTheme.color.greyG4
                )
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickable { onAction(DetailAction.MemberSectionClicked(capsuleId)) },
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }

            Spacer(Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                data.collaborators?.content?.forEach { collaborator ->
                    GlideImage(
                        modifier = Modifier
                            .size(48.dp)
                            .wavyStroke(
                                color = MSTheme.color.greyG5,
                                fillColor = MSTheme.color.white,
                                strokeWidth = 3.dp,
                                cornerRadius = 24.dp,
                                amplitude = 1.dp,
                                spacing = 2.dp,
                                clipContent = true,
                            ),
                        imageModel = { collaborator.profileImageUrl },
                    )
                }
            }
        }
    }
}
}


@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        data = TimeCapsuleData(null),
        capsuleId = 0L,
        showToast = false,
        onAction = {},
    )
}
