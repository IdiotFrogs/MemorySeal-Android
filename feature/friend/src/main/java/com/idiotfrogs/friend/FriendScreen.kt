package com.idiotfrogs.friend

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.friend.component.FriendListItem
import com.idiotfrogs.friend.component.FriendTopNotification
import com.idiotfrogs.model.timecapsule.RequestStatus
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FriendRoute(
    capsuleId: Long,
    viewModel: FriendViewModel = hiltViewModel<FriendViewModel, FriendViewModel.Factory>(key = capsuleId.toString()) { it.create(capsuleId) }
) {
    val navigator = LocalComposeMSNavigator.current
    val clipboard = LocalClipboard.current
    val uiState by viewModel.collectAsState()
    var toastState by remember { mutableStateOf(FriendScreenActionState.IDLE) }

    LaunchedEffect(toastState) {
        if (toastState == FriendScreenActionState.IDLE) return@LaunchedEffect
        delay(2000L)
        toastState = FriendScreenActionState.IDLE
    }

    viewModel.collectSideEffect { event ->
        when (event) {
            FriendSideEffect.NavigateToBack -> navigator.popBackStack()
            is FriendSideEffect.CopyInviteCode -> {
                val clipData = ClipData.newPlainText("inviteCode", event.code)
                clipboard.setClipEntry(ClipEntry(clipData))
            }
            is FriendSideEffect.ShowToast -> toastState = event.state
        }
    }

    when (val state = uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            FriendScreen(
                capsuleId = capsuleId,
                toastState = toastState,
                data = state.data,
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun FriendScreen(
    capsuleId: Long,
    toastState: FriendScreenActionState,
    data: CollaboratorsData,
    onAction: (FriendAction) -> Unit,
) {
    val context = LocalContext.current

    val hazeState = rememberHazeState()
    var expanded by remember { mutableStateOf(false) }
    val menuList by remember {
        mutableStateOf(
            listOf(
                MSMenuFabModel("참여 링크 공유") {
                    expanded = false

//                    val imageUri = FileProvider.getUriForFile(
//                        context,
//                        "${context.packageName}.provider", // provider authority
//                        imageFile // File 객체
//                    )

                    // 공유 인텐트 생성
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
//                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "내가 만든 타임 티켓에 함께해줘! 아래 링크로 참여 요청을 보내면 “타임 캡슐 이름”에 합류할 수 있어요. [초대 링크]"
                        )
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    // 시스템 공유 바텀 시트 표시
                    context.startActivity(
                        Intent.createChooser(shareIntent, "공유하기")
                    )
                },
                MSMenuFabModel("참여 코드 복사") {
                    expanded = false
                    onAction(FriendAction.CopyInviteCode(capsuleId))
                },
            )
        )
    }
    val pendingCollaborators = data.collaborators.filter { it.status == RequestStatus.PENDING }

    Box {
        MSMenuFab(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp),
            expanded = expanded,
            hasFab = false,
            offset = DpOffset(x = 0.dp, y = 40.dp),
            menuList = menuList,
            onClick = { expanded = !expanded },
            onDismiss = { expanded = false },
        )

        if (toastState != FriendScreenActionState.IDLE) {
            FriendTopNotification(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.TopCenter)
                    .systemBarsPadding()
                    .zIndex(1f),
                hazeState = hazeState,
                action = toastState,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .background(MSTheme.color.white)
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                MSDetailHeader(
                    title = "멤버 추가",
                    navigateToBack = { onAction(FriendAction.NavigateToBack) },
                    paddingValues = PaddingValues(horizontal = 0.dp, vertical = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_friend),
                        contentDescription = "메뉴 열기",
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable { expanded = true }
                    )
                }
            }

            if (pendingCollaborators.isEmpty()) {
                item {
                    Spacer(Modifier.height(25.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 75.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_friend_empty_plus),
                            contentDescription = "emptyIcon",
                            modifier = Modifier
                                .size(124.dp, 262.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    MSText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "대기중인 멤버가 없습니다.\n코드 또는 링크로 멤버를 초대해보세요.",
                        color = MSTheme.color.greyG4,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
                items(
                    items = pendingCollaborators,
                    key = { it.requestId },
                ) { item ->
                    FriendListItem(
                        modifier = Modifier.padding(bottom = 8.dp),
                        nickName = item.nickname,
                        profileImageUrl = item.profileImageUrl,
                        onAccept = { onAction(FriendAction.ProcessRequest(item.requestId, true)) },
                        onReject = { onAction(FriendAction.ProcessRequest(item.requestId, false)) },
                    )
                }
            }
        }

        MSDim(
            visible = expanded,
            onDismiss = { expanded = false }
        )
    }
}

@Preview
@Composable
fun FriendScreenPreview() {
    FriendScreen(
        capsuleId = 0L,
        toastState = FriendScreenActionState.IDLE,
        data = CollaboratorsData(),
        onAction = {}
    )
}