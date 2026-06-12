package com.idiotfrogs.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDetailHeader
import com.idiotfrogs.designsystem.component.MSDim
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.management.component.ManagementDeleteContainer
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.resource.R
import com.idiotfrogs.util.UiState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ManagementRoute(
    viewModel: ManagementViewModel = hiltViewModel(),
    capsuleId: Long,
    capsuleTitle: String,
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    var showCannotExitDialog by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { event ->
        when (event) {
            ManagementSideEffect.NavigateToHome -> navigator.navigate(Routes.Home)
            ManagementSideEffect.NavigateToBack -> navigator.popBackStack()
            ManagementSideEffect.NavigateToFriend -> navigator.navigate(Routes.Friend(capsuleId))
        }
    }

    when (uiState) {
        UiState.Init -> Unit
        is UiState.Success -> {
            ManagementScreen(
                capsuleId = capsuleId,
                capsuleTitle = capsuleTitle,
                onAction = viewModel::onAction
            )
        }
        is UiState.Error -> Unit
    }

    if (showCannotExitDialog) {
        MSTitleDialog(
            title = "방장은 방장 권한을 위임 혹은 티켓에 아무도 없을때 티켓을 나가실 수 있어요.",
            onConfirm = { showCannotExitDialog = false },
            onCancel = { showCannotExitDialog = false },
            content = { /** 없음 */ }
        )
    }
}

@Composable
fun ManagementScreen(
    modifier: Modifier = Modifier,
    capsuleId: Long,
    capsuleTitle: String,
    onAction: (ManagementAction) -> Unit,
) {
    var showExitDialog by remember { mutableStateOf(false) }
    var showDeleteContainer by remember { mutableStateOf(false) }

    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeHeight by remember { derivedStateOf { ime.getBottom(density) } }
    val textFieldState = rememberTextFieldState()

    LaunchedEffect(imeHeight) { if (showDeleteContainer && imeHeight == 0) showDeleteContainer = false }

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MSTheme.color.white)
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MSDetailHeader(
                title = "티켓 관리",
                fontSize = 20.dp,
                navigateToBack = { onAction(ManagementAction.NavigateToBack) },
                paddingValues = PaddingValues(horizontal = 0.dp, vertical = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .noRippleClickable { onAction.invoke(ManagementAction.NavigateToFriend) },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .wavyStroke(
                            color = Color(0xFF9EDAAD),
                            amplitude = 1.dp,
                            spacing = 1.dp,
                            fillColor = MSTheme.color.primaryLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_people),
                        contentDescription = "멤버",
                        colorFilter = ColorFilter.tint(MSTheme.color.primaryDark)
                    )
                }
                MSText(
                    text = "멤버",
                    fontSize = 16.dp,
                    fontWeight = FontWeight.SemiBold,
                    color = MSTheme.color.greyG4
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .noRippleClickable { showExitDialog = true },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG1,
                            amplitude = 1.dp,
                            spacing = 1.dp,
                            fillColor = MSTheme.color.bgNormal
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = "나가기",
                        colorFilter = ColorFilter.tint(MSTheme.color.greyG3)
                    )
                }
                MSText(
                    text = "티켓 나가기",
                    fontSize = 16.dp,
                    fontWeight = FontWeight.SemiBold,
                    color = MSTheme.color.greyG4
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .noRippleClickable { showDeleteContainer = true },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .wavyStroke(
                            color = MSTheme.color.greyG1,
                            amplitude = 1.dp,
                            spacing = 1.dp,
                            fillColor = MSTheme.color.bgNormal
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_trashcan),
                        contentDescription = "삭제",
                        colorFilter = ColorFilter.tint(MSTheme.color.greyG3)
                    )
                }
                MSText(
                    text = "티켓 삭제",
                    fontSize = 16.dp,
                    fontWeight = FontWeight.SemiBold,
                    color = MSTheme.color.greyG4
                )
            }
        }
        MSDim(
            visible = showDeleteContainer,
            onDismiss = { showDeleteContainer = false }
        )
        ManagementDeleteContainer(
            isShow = showDeleteContainer,
            textFieldState = textFieldState,
            capsuleTitle = capsuleTitle,
            onDelete = {
                onAction(ManagementAction.DeleteCapsule(capsuleId))
                showDeleteContainer = false
            },
            onCancel = { showDeleteContainer = false }
        )
        if (showExitDialog) {
            MSTitleDialog(
                title = "정말 티켓을 나가시겠습니까?",
                onConfirm = {
                    showExitDialog = false
                    onAction.invoke(ManagementAction.LeaveTimeCapsule(capsuleId))
                },
                cancelText = "취소",
                onCancel = { showExitDialog = false },
                content = {
                    Spacer( modifier = Modifier.height(8.dp))
                    MSText(
                        text = "나가실 경우 다시 초대를 받아야만 들어 오실 수 있습니다.",
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.dp,
                        color = MSTheme.color.greyG3
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            )
        }
    }
}

@Preview
@Composable
fun ManagementScreenPreview() {
    ManagementScreen(capsuleId = 0, capsuleTitle = "티켓 Title") {}
}
