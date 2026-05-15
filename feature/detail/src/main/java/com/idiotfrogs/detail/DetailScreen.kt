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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSCalender
import com.idiotfrogs.designsystem.component.MSDashHorizontalDivider
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.extension.toDday
import com.idiotfrogs.extension.toYearMonthDayWeek
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import com.skydoves.landscapist.glide.GlideImage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DetailRoute(
    capsuleId: Long,
    viewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(key = capsuleId.toString()) { it.create(capsuleId) },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            DetailSideEffect.NavigateToBack -> navigator.popBackStack()
            is DetailSideEffect.NavigateToFriend -> navigator.navigate(Routes.Friend(event.id))
            is DetailSideEffect.NavigateToManagement -> navigator.navigate(
                Routes.Management(
                    id = event.id,
                    title = event.title,
                )
            )
        }
    }

    when (val state = uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            DetailScreen(
                data = state.data,
                capsuleId = capsuleId,
                onAction = viewModel::onAction,
            )
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun DetailScreen(
    data: TimeCapsuleData,
    capsuleId: Long,
    onAction: (DetailAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val capsule = data.capsule
    val status = capsule?.timeCapsuleStatus ?: TimeCapsuleStatus.BEFOREBURIED
    val role = capsule?.userRole ?: TimeCapsuleRole.CONTRIBUTOR
    val isHost = role == TimeCapsuleRole.HOST
    val isBuried = status == TimeCapsuleStatus.BURIED
    var showBuryDialog by remember { mutableStateOf(false) }

    if (showBuryDialog) {
        MSTitleDialog(
            title = "티켓 오픈일 설정",
            confirmText = "묻기",
            cancelText = "취소",
            onConfirm = { showBuryDialog = false },
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
            MSCalender(
                showSealDate = true,
            ) {
                // API 연결 시 선택 날짜를 저장해 묻기 요청에 사용
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MSTheme.color.bgNormal)
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
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
                    .noRippleClickable { onAction(DetailAction.NavigateToBack) },
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
                            DetailAction.NavigateToManagement(
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
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            if (isBuried) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MSText(
                        text = capsule?.openedAt.toDday(),
                        fontSize = 24.dp,
                        color = MSTheme.color.greyG5,
                    )
                    Spacer(Modifier.width(12.dp))
                    VerticalDivider(
                        modifier = Modifier.height(21.dp),
                        thickness = 2.dp,
                        color = MSTheme.color.greyG2,
                    )
                    Spacer(Modifier.width(12.dp))
                    MSText(
                        modifier = Modifier.weight(1f),
                        text = capsule?.title.orEmpty(),
                        fontSize = 20.dp,
                        color = MSTheme.color.greyG5,
                    )
//                    Image(
//                        modifier = Modifier.size(72.dp, 32.dp),
//                        painter = painterResource(R.drawable.img_detail_buried_badge),
//                        contentDescription = "묻혀있음",
//                    )
                }
            } else {
                MSText(
                    text = capsule?.title.orEmpty(),
                    fontSize = 20.dp,
                    color = MSTheme.color.greyG5,
                )
            }

            Spacer(Modifier.height(12.dp))

            MSText(
                text = capsule?.createdAt.toYearMonthDayWeek() + " ~ " + capsule?.openedAt.toYearMonthDayWeek(),
                fontSize = 14.dp,
                fontWeight = FontWeight.Medium,
                color = MSTheme.color.greyG3,
            )

            Spacer(Modifier.height(12.dp))

            MSText(
                text = capsule?.description.orEmpty(),
                fontSize = 16.dp,
                fontWeight = FontWeight.Normal,
                color = MSTheme.color.greyG5,
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
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(R.drawable.ic_detail_rigt),
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
                        text = "2개 등록",
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
                        text = "12개 등록",
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
                        ) { append(data.collaborators.size.toString()) }
                    },
                    color = MSTheme.color.greyG4
                )
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickable { onAction(DetailAction.NavigateToFriend(capsuleId)) },
                    painter = painterResource(R.drawable.ic_detail_rigt),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }

            Spacer(Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                data.collaborators.forEach { collaborator ->
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

@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        data = TimeCapsuleData(null),
        capsuleId = 0L,
        onAction = {},
    )
}
