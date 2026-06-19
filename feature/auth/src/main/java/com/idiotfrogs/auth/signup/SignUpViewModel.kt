package com.idiotfrogs.auth.signup

import com.idiotfrogs.domain.usecase.user.SignUpUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpUiState, SignUpSideEffect, SignUpAction>() {

    override val container: Container<SignUpUiState, SignUpSideEffect> = container(SignUpUiState())

    override fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.SignUp -> signUp(action.nickname, action.file)
            SignUpAction.NavigateToBack -> intent { postSideEffect(SignUpSideEffect.NavigateToBack) }
        }
    }

    private fun signUp(nickname: String, file: File?) {
        if (file == null) return

        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            val result = signUpUseCase(nickname, file)

            result.onSuccess {
                intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
                intent { postSideEffect(SignUpSideEffect.NavigateToHome) }
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            }
        }
    }
}

data class SignUpUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface SignUpAction {
    data class SignUp(val nickname: String, val file: File?): SignUpAction
    data object NavigateToBack : SignUpAction
}

sealed interface SignUpSideEffect {
    data object NavigateToHome : SignUpSideEffect
    data object NavigateToBack : SignUpSideEffect
}
