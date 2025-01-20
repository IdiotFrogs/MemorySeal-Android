package com.idiotfrogs.auth

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    // TODO: Login button guideline에 맞게 디자인 수정
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { loginViewModel.googleLogin() }
        ) {
            Text(text = "google login")
        }

        Button(
            onClick = { loginViewModel.appleLogin() }
        ) {
            Text(text = "apple login")
        }
    }

    when(val state = uiState) {
        is LoginUiState.Error -> {
            // 에러 처리 로직
            // TODO: 에러 처리 Dialog or Screen 출력
            Log.d("LoginError", state.errorMessage.toString())
        }
        LoginUiState.Init -> {
            // 화면 로딩 로직
        }
        LoginUiState.Success -> {
            // 화면 로직
            // TODO: 메인 화면으로 navigation 처리
        }
    }
}