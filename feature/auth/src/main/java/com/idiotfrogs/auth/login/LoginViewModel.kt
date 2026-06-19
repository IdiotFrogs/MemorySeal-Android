package com.idiotfrogs.auth.login

import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase
) : BaseViewModel<LoginUiState, LoginSideEffect, LoginAction>() {

    override val container: Container<LoginUiState, LoginSideEffect> = container(
        initialState = LoginUiState(),
        onCreate = {
            safeLaunch {
                // TODO 자동 로그인 체크
                intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
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
            intent { reduce { state.copy(isLoading = true) } }

            try {
                loginCallback()
                val result = getMyProfileUseCase()

                result.onSuccess { profile ->
                    intent {
                        reduce { state.copy(isLoading = false, errorMessage = null) }
                        if (profile.isOnboarding) postSideEffect(LoginSideEffect.NavigateToHome)
                        else postSideEffect(LoginSideEffect.NavigateToSignUp)
                    }
                }.onFailure {
                    intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                }
            } catch (throwable: Throwable) {
                intent { reduce { state.copy(isLoading = false, errorMessage = throwable.message) } }
                throw throwable
            }
        }
    }
}

data class LoginUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface LoginAction {
    data class SocialLogin(val loginCallback: suspend () -> Unit) : LoginAction
}

sealed interface LoginSideEffect {
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToHome : LoginSideEffect
}
