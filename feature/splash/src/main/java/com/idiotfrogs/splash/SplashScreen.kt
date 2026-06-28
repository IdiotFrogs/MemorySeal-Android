package com.idiotfrogs.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.idiotfrogs.designsystem.component.MSLoadingOverlay
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.component.MSTitleDialog
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val context = LocalContext.current
    val uiState by viewModel.collectAsState()

    var showPermissionDialog by remember { mutableStateOf(false) }

    // TIRAMISU 이상인 경우에는 알림 권한 필요
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(notificationPermission.status) {
            when {
                notificationPermission.status.isGranted -> viewModel.onAction(SplashAction.AutoLoginRequested)
                notificationPermission.status.shouldShowRationale -> showPermissionDialog = true // 권한 거부
                else -> notificationPermission.launchPermissionRequest() // 권한 요청
            }
        }
    }

    viewModel.collectSideEffect { event ->
        when (event) {
            SplashSideEffect.NavigateToHome -> {
                navigator.clear()
                navigator.navigate(Routes.Home)
            }
            SplashSideEffect.NavigateToLogin -> {
                navigator.clear()
                navigator.navigate(Routes.Login)
            }
            SplashSideEffect.NavigateToSignUp -> {
                navigator.clear()
                navigator.navigate(Routes.SignUp)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SplashScreen()
        MSLoadingOverlay(visible = uiState.isLoading)
    }

    if (showPermissionDialog) {
        MSTitleDialog(
            title = "알림 권한을 허용해주세요.",
            onConfirm = context::openSetting,
            onCancel = { /** no-op */ },
            content = {
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = "설정을 하지 않을 시 서비스 이용이 불가합니다.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    color = MSTheme.color.greyG4
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }
}

@Composable
fun SplashScreen() { /** todo: 추후 구현 */ }

private fun Context.openSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", this@openSetting.packageName, null)
    }
    this.startActivity(intent)
}
