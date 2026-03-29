package com.idiotfrogs.setting

import androidx.lifecycle.ViewModel
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(

): BaseViewModel<SettingAction>() {
    private val _event = MutableSharedFlow<SettingEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: SettingAction) {
        when (action) {
            SettingAction.NavigateToLogin -> navigateToLogin()
            SettingAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun navigateToLogin() {
        safeLaunch { _event.emit(SettingEvent.NavigateToLogin) }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(SettingEvent.NavigateToBack) }
    }
}

sealed interface SettingAction {
    data object NavigateToLogin : SettingAction
    data object NavigateToBack : SettingAction
}

sealed interface SettingEvent {
    data object NavigateToLogin : SettingEvent
    data object NavigateToBack : SettingEvent
}