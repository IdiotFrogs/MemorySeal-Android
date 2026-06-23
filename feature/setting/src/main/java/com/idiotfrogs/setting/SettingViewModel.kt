package com.idiotfrogs.setting

import com.idiotfrogs.domain.usecase.user.WithdrawUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase
) : BaseViewModel<SettingUiState, SettingSideEffect, SettingAction>() {

    override val container: Container<SettingUiState, SettingSideEffect> = container(SettingUiState())

    override fun onAction(action: SettingAction) {
        when (action) {
            SettingAction.LogoutConfirmed -> intent { postSideEffect(SettingSideEffect.NavigateToLogin) }
            SettingAction.BackClicked -> intent { postSideEffect(SettingSideEffect.NavigateToBack) }
            SettingAction.WithdrawConfirmed -> withdraw()
        }
    }

    private fun withdraw() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            withdrawUseCase()
                .onSuccess {
                    intent {
                        reduce { state.copy(isLoading = false, errorMessage = null) }
                        postSideEffect(SettingSideEffect.NavigateToLogin)
                    }
                 }.onFailure {
                    intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                 }
        }
    }
}

data class SettingUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface SettingAction {
    data object LogoutConfirmed : SettingAction
    data object BackClicked : SettingAction
    data object WithdrawConfirmed : SettingAction
}

sealed interface SettingSideEffect {
    data object NavigateToLogin : SettingSideEffect
    data object NavigateToBack : SettingSideEffect
}
