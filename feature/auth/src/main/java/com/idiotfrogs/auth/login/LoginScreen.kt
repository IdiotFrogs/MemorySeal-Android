package com.idiotfrogs.auth.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.idiotfrogs.auth.util.LocalLoginManager

@Composable
fun LoginRoute(
    loginViewModel: LoginViewModel = viewModel(),
    navigateToErrorScreen: (String) -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val loginManager = LocalLoginManager.current

    when (val state = uiState) {
        is LoginUiState.Error -> {
            navigateToErrorScreen(state.errorMessage.toString())
        }
        LoginUiState.Init -> {
            // 화면 로딩 로직
        }
        LoginUiState.UiLoaded -> {
            if (loginManager != null) {
                LoginScreen(
                    googleLogin = {
                        loginViewModel.socialLogin { loginManager.googleLogin() }
                    },
                    appleLogin = {
                        loginViewModel.socialLogin { loginManager.appleLogin() }
                    }
                )
            } else {
                navigateToErrorScreen("LoginManager is null")
            }
        }
        LoginUiState.LoginSuccess -> {} // TODO: 메인 화면으로 navigation 처리
    }
}

@Composable
fun LoginScreen(
    googleLogin: () -> Unit,
    appleLogin: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = googleLogin) {
            Text(text = "google login")
        }
        Button(onClick = appleLogin) {
            Text(text = "apple login")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        googleLogin = {},
        appleLogin = {},
    )
}