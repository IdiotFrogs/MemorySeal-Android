package com.idiotfrogs.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SampleScreen(
    sampleViewModel: SampleViewModel
) {
    val uiState by sampleViewModel.uiState.collectAsStateWithLifecycle()

    when(uiState) {
        is SampleUiState.Error -> {
            // 에러 처리 로직
        }
        SampleUiState.Init -> {
            // 화면 로딩 로직
        }
        SampleUiState.Success -> {
            // 화면 로직
        }
    }
}