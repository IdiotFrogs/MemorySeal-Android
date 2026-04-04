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
    override fun onAction(action: SplashAction) { }

    override val container: Container<UiState<Unit>, SplashSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                /** todo: 데이터 로드 등 사전 작업 */
                intent { reduce { UiState.Success(Unit) } }
                autoLogin()
            }
        }
    )

    private fun autoLogin() {
        intent {
            if (getAccessTokenUseCase.accessToken.firstOrNull() == null) {
                postSideEffect(SplashSideEffect.NavigateToLogin)
            } else {
                getMyProfileUseCase()
                    .onSuccess {
                        if (it.isOnboarding) {
                            postSideEffect(SplashSideEffect.NavigateToHome)
                        } else {
                            postSideEffect(SplashSideEffect.NavigateToSignUp)
                        }
                    }
                    .onFailure {
                        postSideEffect(SplashSideEffect.NavigateToLogin)
                    }
            }
        }
    }
}

sealed interface SplashAction

sealed interface SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToHome : SplashSideEffect
    data object NavigateToSignUp : SplashSideEffect
}