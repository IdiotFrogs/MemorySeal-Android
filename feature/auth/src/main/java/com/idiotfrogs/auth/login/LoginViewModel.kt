package com.idiotfrogs.auth.login

import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase
) : BaseViewModel<UiState<Unit>, LoginSideEffect, LoginAction>() {

    override val container: Container<UiState<Unit>, LoginSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                // TODO 자동 로그인 체크
                intent { reduce { UiState.Success(Unit) } }
            }
        }
    )

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.SocialLogin -> socialLogin(action.loginCallback)
        }
    }

    private fun socialLogin(loginCallback: suspend () -> Unit) {
        safeLaunch {
            loginCallback()
            val result = getMyProfileUseCase()

            result.onSuccess { profile ->
                intent {
                    if (profile.isOnboarding) postSideEffect(LoginSideEffect.NavigateToHome)
                    else postSideEffect(LoginSideEffect.NavigateToSignUp)
                }
            }.onFailure {
                intent { reduce { UiState.Error(errorMessage = it.message) } }
            }
        }
    }
}

sealed interface LoginAction {
    data class SocialLogin(val loginCallback: suspend () -> Unit): LoginAction
}

sealed interface LoginSideEffect {
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToHome : LoginSideEffect
}