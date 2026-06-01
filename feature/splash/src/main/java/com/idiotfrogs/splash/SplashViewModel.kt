package com.idiotfrogs.splash

import com.idiotfrogs.domain.usecase.local.GetAccessTokenUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
): BaseViewModel<UiState<Unit>, SplashSideEffect, SplashAction>() {
    override val container: Container<UiState<Unit>, SplashSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            /** todo: 데이터 로드 등 사전 작업 */
            intent { reduce { UiState.Success(Unit) } }
        }
    )

    override fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.AutoLogin -> autoLogin()
        }
    }

    private fun autoLogin() {
        safeLaunch {
            if (getAccessTokenUseCase.accessToken.firstOrNull() == null) {
                intent { postSideEffect(SplashSideEffect.NavigateToLogin) }
            } else {
                val result = getMyProfileUseCase()
                intent {
                    result.onSuccess {
                        if (it.isOnboarding) {
                            postSideEffect(SplashSideEffect.NavigateToHome)
                        } else {
                            postSideEffect(SplashSideEffect.NavigateToSignUp)
                        }
                    }.onFailure {
                        postSideEffect(SplashSideEffect.NavigateToLogin)
                    }
                }
            }
        }
    }
}

sealed interface SplashAction {
    data object AutoLogin : SplashAction
}

sealed interface SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToHome : SplashSideEffect
    data object NavigateToSignUp : SplashSideEffect
}