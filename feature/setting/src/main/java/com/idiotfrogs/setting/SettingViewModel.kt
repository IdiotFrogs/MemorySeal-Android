package com.idiotfrogs.setting

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(

) : BaseViewModel<UiState<Unit>, SettingSideEffect, SettingAction>() {

    override val container: Container<UiState<Unit>, SettingSideEffect> = container(UiState.Success(Unit))

    override fun onAction(action: SettingAction) {
        when (action) {
            SettingAction.NavigateToLogin -> intent { postSideEffect(SettingSideEffect.NavigateToLogin) }
            SettingAction.NavigateToBack -> intent { postSideEffect(SettingSideEffect.NavigateToBack) }
        }
    }
}

sealed interface SettingAction {
    data object NavigateToLogin : SettingAction
    data object NavigateToBack : SettingAction
}

sealed interface SettingSideEffect {
    data object NavigateToLogin : SettingSideEffect
    data object NavigateToBack : SettingSideEffect
}