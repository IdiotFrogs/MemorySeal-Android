package com.idiotfrogs.detail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSAnnotatedText
import com.idiotfrogs.designsystem.component.MSDialog
import com.idiotfrogs.designsystem.component.MSMessageItem
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MessageType
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.detail.component.MembarListItem
import com.idiotfrogs.detail.component.RoundedProgressBar
import com.idiotfrogs.extension.toDday
import com.idiotfrogs.extension.toYearMonthDayWeek
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
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
    isVoteStart: Boolean,
    iSSeal: Boolean,
    viewModel: DetailViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(key = capsuleId.toString()) { it.create(capsuleId) },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            DetailSideEffect.NavigateToBack -> navigator.popBackStack()
            is DetailSideEffect.NavigateToFriend -> navigator.navigate(Routes.Friend(event.id))
            is DetailSideEffect.NavigateToManagement -> navigator.navigate(Routes.Management(event.id))
        }
    }

    when (val state = uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            DetailScreen(
                data = state.data,
                capsuleId = capsuleId,
                isVoteStart = isVoteStart,
                iSSeal = iSSeal,
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun DetailScreen(
    data: TimeCapsuleData,
    capsuleId: Long,
    isVoteStart: Boolean,
    iSSeal: Boolean,
    onAction: (DetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showVoteDialog by remember { mutableStateOf(false) }
    val messageList = emptyList<String>()

    if (showVoteDialog) {
        MSDialog(
            title = "봉인 투표에 반대 하시겠습니까?",
            content = "투표를 반대하면 투표를 다시 시작해야 합니다.",
            onConfirm = {
                onAction(DetailAction.SealVote(false))
                showVoteDialog = false
            },
            onCancel = { showVoteDialog = false },
            confirmText = "반대",
            confirmButtonColor = MSTheme.color.black
        )
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MSTheme.color.bgNormal)
            .padding(0.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            data.capsule?.mainImageUrl?.let {
                GlideImage(
                    modifier = Modifier.matchParentSize(),
                    imageModel = { it }
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
                    .noRippleClickable { onAction(DetailAction.NavigateToBack) },
                painter = painterResource(R.drawable.img_close),
                contentDescription = "Close"
            )
            Image(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp)
                    .noRippleClickable { onAction(DetailAction.NavigateToManagement(capsuleId)) },
                painter = painterResource(R.drawable.img_management),
                contentDescription = "Management"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(110.dp)
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))
            )
            MSText(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 38.dp),
                text = data.capsule?.openedAt.toDday(),
                fontSize = 40.dp,
                color = MSTheme.color.white
            )
            MSText(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 24.dp),
                text = data.capsule?.createdAt.toYearMonthDayWeek() + " ~ " + data.capsule?.openedAt.toYearMonthDayWeek(),
                fontSize = 12.dp,
                color = MSTheme.color.white
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            MSText(
                text = data.capsule?.title ?: "",
                fontSize = 20.dp,
            )
            Spacer(Modifier.height(8.dp))
            MSText(
                text = data.capsule?.description ?: "",
                fontSize = 16.dp,
                fontWeight = FontWeight.Normal
            )
            if (!iSSeal) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MSText(text = "티켓 봉인 투표")
                    if (isVoteStart) {
                        MSAnnotatedText(
                            text = buildAnnotatedString {
                                append("1  ")
                                withStyle(
                                    SpanStyle(color = MSTheme.color.greyG4)
                                ) { append("/  " + data.collaborators.size.toString()) }
                            },
                            color = MSTheme.color.primaryNormal
                        )
                    } else {
                        MSButton(
                            onClick = { onAction(DetailAction.SealVote(true)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MSTheme.color.greyG5
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.5.dp)
                        ) {
                            MSText(
                                text = "티켓 봉인하기",
                                fontSize = 14.dp,
                                color = MSTheme.color.white
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (data.capsule?.userRole == TimeCapsuleRole.CONTRIBUTOR) {
                    if (isVoteStart) { // TODO isVoteStart 변수 관리 방법 고민 필요
                        Spacer(Modifier.height(8.dp))
                        RoundedProgressBar(0.2f)
                        Spacer(Modifier.height(24.dp))
                        Row {
                            MSButton(
                                modifier = Modifier.weight(1f),
                                onClick = { showVoteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG1
                                ),
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "반대",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.greyG4
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            MSButton(
                                modifier = Modifier.weight(3f),
                                onClick = { onAction(DetailAction.SealVote(true)) },
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "찬성",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.white
                                )
                            }
                        }
                    } else {
                        MSText(
                            text = "티켓 봉인을 봉인하려면 방장이 투표를 시작하고, 모든 맴버의 동의가 필요합니다.",
                            fontSize = 16.dp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                } else {
                    if (isVoteStart) {
                        Spacer(Modifier.height(8.dp))
                        RoundedProgressBar(0.2f)
                        Spacer(Modifier.height(24.dp))
                        Row {
                            MSButton(
                                modifier = Modifier.weight(1f),
                                onClick = { showVoteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MSTheme.color.greyG1
                                ),
                                contentPadding = PaddingValues(11.dp)
                            ) {
                                MSText(
                                    text = "투표 취소",
                                    fontSize = 16.dp,
                                    color = MSTheme.color.greyG4
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MSText(
                    text = "추억 메시지",
                    color = MSTheme.color.greyG4
                )
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(R.drawable.ic_detail_rigt),
                    contentDescription = "추억 메시지 상세 아이콘"
                )
            }
            Spacer(Modifier.height(24.dp))
            if (messageList.isEmpty()) {
                MSButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    contentPadding = PaddingValues(11.dp)
                ) {
                    MSText(
                        text = "등록하기",
                        fontSize = 16.dp,
                        color = MSTheme.color.white
                    )
                }
            } else {
                MSMessageItem(
                    type = MessageType.TEXT,
                    text = "테스트 문구 입니다.",
                    isSeal = iSSeal
                )
                Spacer(Modifier.height(8.dp))
                MSMessageItem(
                    type = MessageType.PHOTO,
                    imageList = messageList,
                    isSeal = iSSeal
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MSTheme.color.white)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            val myData = data.collaborators.firstOrNull { it.isMe }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
            Spacer(Modifier.height(24.dp))
            MembarListItem(
                nickName = myData?.nickname + " (나)",
                profileImageUrl = myData?.profileImageUrl ?: "",
                isMembar = false
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MSTheme.color.greyG1
            )
            data.collaborators.forEach {
                Spacer(Modifier.height(16.dp))
                MembarListItem(
                    nickName = it.nickname,
                    profileImageUrl = it.profileImageUrl
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        TimeCapsuleData(null),
        0L,
        true,
        false,
        onAction = {}
    )
}