package com.idiotfrogs.splash

import com.idiotfrogs.domain.usecase.local.GetAccessTokenUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.util.base.BaseUiState
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
): BaseViewModel<SplashUiState, SplashSideEffect, SplashAction>() {
    override val container: Container<SplashUiState, SplashSideEffect> = container(
        initialState = SplashUiState(),
        onCreate = {
            /** todo: 데이터 로드 등 사전 작업 */
            intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
        }
    )

    override fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.AutoLoginRequested -> autoLogin()
        }
    }

    private fun autoLogin() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            if (getAccessTokenUseCase.accessToken.firstOrNull() == null) {
                intent {
                    reduce { state.copy(isLoading = false, errorMessage = null) }
                    postSideEffect(SplashSideEffect.NavigateToLogin)
                }
            } else {
                val result = getMyProfileUseCase()
                intent {
                    reduce { state.copy(isLoading = false, errorMessage = null) }
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

data class SplashUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface SplashAction {
    data object AutoLoginRequested : SplashAction
}

sealed interface SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToHome : SplashSideEffect
    data object NavigateToSignUp : SplashSideEffect
}
