package com.idiotfrogs.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current
    val context = LocalContext.current

    // TIRAMISU 이상인 경우에는 알림 권한 필요
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            when {
                notificationPermission.status.isGranted -> return@LaunchedEffect // 권한 이미 허용됨
                notificationPermission.status.shouldShowRationale -> context.openSetting() // 권한 거부 시 설정 창
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

    SplashScreen()
}

@Composable
fun SplashScreen() { /** todo: 추후 구현 */ }

private fun Context.openSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", this@openSetting.packageName, null)
    }
    this.startActivity(intent)
}