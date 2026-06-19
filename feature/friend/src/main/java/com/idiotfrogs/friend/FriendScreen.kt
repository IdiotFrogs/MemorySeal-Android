package com.idiotfrogs.friend

import android.content.ClipData
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDashHorizontalDivider
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.friend.component.FriendBottomSheetItem
import com.idiotfrogs.friend.component.FriendDialogState
import com.idiotfrogs.friend.component.FriendInviteButton
import com.idiotfrogs.friend.component.FriendListItem
import com.idiotfrogs.friend.component.FriendTopNotification
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponseData
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
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
            is FriendSideEffect.CopyInviteCodeToClipboard -> {
                val clipData = ClipData.newPlainText("inviteCode", event.code)
                clipboard.setClipEntry(ClipEntry(clipData))
            }
            is FriendSideEffect.ShowToast -> toastState = event.state
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.data?.let { data ->
            FriendScreen(
                capsuleId = capsuleId,
                toastState = toastState,
                data = data,
                onAction = viewModel::onAction,
            )
        }

        MSLoadingOverlay(visible = uiState.data != null && uiState.isLoading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    capsuleId: Long,
    toastState: FriendScreenActionState,
    data: CollaboratorsData,
    onAction: (FriendAction) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val hazeState = rememberHazeState()
    val searchTextFieldState = rememberTextFieldState()
    var selectedMember by remember { mutableStateOf<TimeCapsuleCollaboratorsResponseData?>(null) }
    var dialogState by remember { mutableStateOf<FriendDialogState>(FriendDialogState.None) }
    var currentUserRole by remember { mutableStateOf<TimeCapsuleRole?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val collaborators = data.collaborators
    val members = collaborators?.content.orEmpty()

    LaunchedEffect(members) {
        members.firstOrNull { it.isMe }?.contributorRole?.let {
            currentUserRole = it
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .background(MSTheme.color.white)
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                MSDetailHeader(
                    title = "멤버",
                    navigateToBack = { onAction(FriendAction.BackClicked) },
                    paddingValues = PaddingValues(horizontal = 0.dp, vertical = 16.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FriendInviteButton(
                        modifier = Modifier.weight(1f),
                        text = "참여 링크 공유",
                        icon = R.drawable.img_share,
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "내가 만든 타임 티켓에 함께해줘! 아래 링크로 참여 요청을 보내면 타임 캡슐에 합류할 수 있어요. [초대 링크]"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
                        },
                    )
                    FriendInviteButton(
                        modifier = Modifier.weight(1f),
                        text = "참여 코드 복사",
                        icon = R.drawable.img_copy,
                        onClick = { onAction(FriendAction.InviteCodeCopyClicked(capsuleId)) },
                    )
                }

                Spacer(Modifier.height(20.dp))

                MSTextField(
                    modifier = Modifier.fillMaxWidth(),
                    hint = "검색",
                    textFieldState = searchTextFieldState,
                    focusedBorderColor = MSTheme.color.greyG1,
                    unfocusedBorderColor = MSTheme.color.greyG1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    onKeyboardAction = { performDefaultAction ->
                        onAction(FriendAction.SearchSubmitted(searchTextFieldState.text.toString()))
                        performDefaultAction()
                        focusManager.clearFocus()
                    },
                    cursorBrush = SolidColor(MSTheme.color.greyG5),
                    fillColor = MSTheme.color.bgNormal,
                    leadingContent = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.img_search),
                            contentDescription = "검색",
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                )

                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MSText(text = "멤버")
                    Spacer(Modifier.width(4.dp))
                    MSText(
                        text = (collaborators?.totalElements ?: members.size).toString(),
                        color = MSTheme.color.primaryDark,
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            items(
                items = members,
                key = { it.userId },
            ) { member ->
                FriendListItem(
                    modifier = Modifier.padding(bottom = 16.dp),
                    nickName = member.nickname,
                    profileImageUrl = member.profileImageUrl,
                    isMe = member.isMe,
                    role = member.contributorRole,
                    onMoreClick = if (currentUserRole == TimeCapsuleRole.HOST && !member.isMe) {
                        { selectedMember = member }
                    } else {
                        null
                    },
                )
            }
        }

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

        if (selectedMember != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedMember = null },
                sheetState = bottomSheetState,
                dragHandle = null,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = MSTheme.color.white,
                scrimColor = Color(0x3D444444),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 30.dp),
                ) {
                    FriendBottomSheetItem(
                        text = "방장 위임",
                        icon = R.drawable.img_host,
                        onClick = {
                            selectedMember?.let {
                                dialogState = FriendDialogState.Delegation(it)
                                selectedMember = null
                            }
                        },
                    )
                    Spacer(Modifier.height(16.dp))
                    MSDashHorizontalDivider()
                    Spacer(Modifier.height(16.dp))
                    FriendBottomSheetItem(
                        text = "멤버 추방",
                        icon = R.drawable.img_kick,
                        onClick = {
                            selectedMember?.let {
                                dialogState = FriendDialogState.Delete(it)
                                selectedMember = null
                            }
                        },
                    )
                }
            }
        }
    }

    when (val state = dialogState) {
        FriendDialogState.None -> Unit
        is FriendDialogState.Delegation -> {
            MSTitleDialog(
                title = "\"${state.member.nickname}\"님에게\n방장을 위임하시겠습니까?",
                confirmText = "위임",
                cancelText = "취소",
                onConfirm = {
                    onAction(FriendAction.DelegationHostConfirmed(state.member.userId))
                    dialogState = FriendDialogState.None
                },
                onCancel = { dialogState = FriendDialogState.None },
            )
        }
        is FriendDialogState.Delete -> {
            MSTitleDialog(
                title = "\"${state.member.nickname}\"님을\n정말 추방하시겠습니까?",
                confirmText = "추방",
                cancelText = "취소",
                confirmButtonColor = Color(0xFFED1E2F),
                onConfirm = {
                    onAction(FriendAction.DeleteContributorConfirmed(state.member.userId))
                    dialogState = FriendDialogState.None
                },
                onCancel = { dialogState = FriendDialogState.None },
            )
        }
    }
}

@Preview
@Composable
fun FriendScreenPreview() {
    FriendScreen(
        capsuleId = 0L,
        toastState = FriendScreenActionState.IDLE,
        data = CollaboratorsData(
            collaborators = TimeCapsuleCollaboratorsResponse(
                content = listOf(
                    TimeCapsuleCollaboratorsResponseData(
                        contributorRole = TimeCapsuleRole.CONTRIBUTOR,
                        nickname = "검정 복숭아",
                        userId = 1L,
                        profileImageUrl = "",
                        userActiveStatus = true,
                        isMe = false,
                    ),
                    TimeCapsuleCollaboratorsResponseData(
                        contributorRole = TimeCapsuleRole.HOST,
                        nickname = "민트 네모 수박",
                        userId = 2L,
                        profileImageUrl = "",
                        userActiveStatus = true,
                        isMe = true,
                    ),
                    TimeCapsuleCollaboratorsResponseData(
                        contributorRole = TimeCapsuleRole.CONTRIBUTOR,
                        nickname = "초코 체리",
                        userId = 3L,
                        profileImageUrl = "",
                        userActiveStatus = true,
                        isMe = false,
                    ),
                ),
                totalPages = 1,
                totalElements = 3,
                number = 0,
                last = true,
            ),
        ),
        onAction = {},
    )
}
