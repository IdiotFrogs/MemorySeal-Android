package com.idiotfrogs.open

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.idiotfrogs.designsystem.util.noRippleClickable
import com.idiotfrogs.navigation.LocalComposeMSNavigator
import com.idiotfrogs.navigation.Routes
import com.idiotfrogs.open.component.OpenAnimation
import com.idiotfrogs.open.component.OpenInteraction
import com.idiotfrogs.resource.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

enum class OpenStep { INTERACTION, ANIMATION }

@Composable
fun OpenRoute(
    capsuleId: Long,
    viewModel: OpenViewModel = hiltViewModel<OpenViewModel, OpenViewModel.Factory>(key = capsuleId.toString()) { it.create(capsuleId) },
) {
    val navigator = LocalComposeMSNavigator.current
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { event ->
        when (event) {
            is OpenSideEffect.NavigateToDetail -> navigator.navigate(Routes.Detail(event.capsuleId))
        }
    }

    uiState.data?.let { data ->
        OpenedScreen(
            data = data,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun OpenedScreen(
    data: OpenData,
    onAction: (OpenAction) -> Unit,
) {
    var currentOpenStep by remember { mutableStateOf(OpenStep.INTERACTION) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ticket_open))
    val ready = composition != null
    val visibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(currentOpenStep, ready) {
        if (currentOpenStep == OpenStep.ANIMATION && ready) {
            visibleState.targetState = true
        }
    }

    when (currentOpenStep) {
        OpenStep.INTERACTION -> {
            OpenInteraction(
                image = data.imageUrl,
                onFinish = { currentOpenStep = OpenStep.ANIMATION}
            )
        }
        // 가드 조건(if) - 매칭된 조건에 대한 추가 검사 제공
        OpenStep.ANIMATION if ready -> {
            Box(
                modifier = Modifier
                    .noRippleClickable { /** no-op */ }
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(tween(500))
                ) {
                    OpenAnimation(
                        image = data.imageUrl,
                        composition = { composition!! }, // 위에서 체크해서 non-null, 람다를 통한 지연 읽기
                        confirmClick = { onAction.invoke(OpenAction.DoneClick) }
                    )
                }
            }
        }
        else -> Unit
    }
}