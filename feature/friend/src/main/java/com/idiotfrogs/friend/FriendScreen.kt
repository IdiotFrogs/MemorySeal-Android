package com.idiotfrogs.friend

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.idiotfrogs.designsystem.component.MSMenuFab
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.model.MSMenuFabModel
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.friend.component.FriendListItem
import com.idiotfrogs.friend.component.FriendTopNotification
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FriendRoute(
    viewModel: FriendViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            FriendSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            FriendScreen(onAction = viewModel::onAction)
        }
        is UiState.Error -> Unit
    }
}

@Composable
fun FriendScreen(
    modifier: Modifier = Modifier,
    onAction: (FriendAction) -> Unit,
) {
    val context = LocalContext.current

    var isEmpty by remember { mutableStateOf(false) }
    var action by remember { mutableStateOf(FriendScreenActionState.IDLE) }
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
                    action = FriendScreenActionState.COPY
                },
            )
        )
    }

    LaunchedEffect(action) {
        delay(1000L)
        action = FriendScreenActionState.IDLE
    }

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

        if (action != FriendScreenActionState.IDLE) {
            FriendTopNotification(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.TopCenter)
                    .navigationBarsPadding()
                    .zIndex(1f),
                action = action,
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MSTheme.color.white)
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MSDetailHeader(
                title = "맴버 추가",
                navigateToBack = { onAction(FriendAction.NavigateToBack) },
                paddingValues = PaddingValues(horizontal = 0.dp, vertical = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Back",
                    modifier = Modifier.noRippleClickable { expanded = true }
                )
            }
            if (isEmpty) {
                Spacer(Modifier.height(25.dp))
                Icon(
                    painter = painterResource(R.drawable.img_friend_empty_plus),
                    contentDescription = "emptyIcon",
                    tint = MSTheme.color.greyG1,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 40.dp)
                )
                Spacer(Modifier.height(32.dp))
                MSText(
                    text = "대기중인 맴버가 없습니다.\n" + "코드 또는 링크로 맴버를 초대해보세요.",
                    color = MSTheme.color.greyG4,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            } else {
                repeat(10) { // TODO 테스트용 코드 -> 추 후 실제 list 변경 필요
                    Spacer(Modifier.height(8.dp))
                    FriendListItem(
                        nickName = when (it) {
                            0 -> "파란 바나나"
                            1 -> "검정 복숭아"
                            2 -> "별 모양 파인애플"
                            3 -> "초코 체리"
                            4 -> "자두 수박"
                            else -> "민트 네모 수박"
                        },
                        onAccept = { action = FriendScreenActionState.ACCEPT },
                        onReject = { action = FriendScreenActionState.REJECT }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun FriendScreenPreview() {
    FriendScreen(onAction = {})
}