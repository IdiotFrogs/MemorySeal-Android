package com.idiotfrogs.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.designsystem.component.MSDialog
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.setting.component.SettingHeader
import com.idiotfrogs.setting.component.SettingItem
import com.idiotfrogs.setting.component.SettingType
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            SettingSideEffect.NavigateToLogin -> navigator.navigate(Routes.Login)
            SettingSideEffect.NavigateToBack -> navigator.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SettingScreen(onAction = viewModel::onAction)

        MSLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
fun SettingScreen(
    onAction: (SettingAction) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        MSTitleDialog(
            title = "로그아웃",
            confirmText = "로그아웃",
            cancelText = "유지",
            onConfirm = {
                /** TODO: 로그아웃 로직 */
                showLogoutDialog = false
                onAction(SettingAction.LogoutConfirmed)
            },
            onCancel = { showLogoutDialog = false },
            content = {
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = "메실에서 로그아웃 하시겠습니까?",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }

    if (showWithdrawDialog) {
        MSTitleDialog(
            title = "회원탈퇴",
            confirmText = "탈퇴",
            cancelText = "취소",
            onConfirm = {
                showWithdrawDialog = false
                onAction(SettingAction.WithdrawConfirmed)
            },
            onCancel = { showWithdrawDialog = false },
            content = {
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = "메실 회원을 탈퇴하시겠습니까?\n티켓에 저장된 내용은 삭제되지 않습니다.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
            .systemBarsPadding(),
    ) {
        SettingHeader(onBack = { onAction(SettingAction.BackClicked) })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingItem(
                settingType = SettingType.Text(content = "v0.2"),
                title = "앱 버전"
            )
            SettingItem(
                settingType = SettingType.Button,
                title = "이용 약관",
                onClick = { /** TODO: 약관 관련 정책 수립 필요 */ }
            )
            SettingItem(
                settingType = SettingType.Button,
                title = "로그아웃",
                onClick = { showLogoutDialog = true }
            )
            SettingItem(
                settingType = SettingType.Button,
                title = "회원탈퇴",
                titleColor = MSTheme.color.red,
                onClick = { showWithdrawDialog = true }
            )
        }
    }
}

@Preview
@Composable
private fun SettingScreenPreview() {
    SettingScreen(onAction = {})
}
