package com.idiotfrogs.splash

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigator = LocalComposeMSNavigator.current

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