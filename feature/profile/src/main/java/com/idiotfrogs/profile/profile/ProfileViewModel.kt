package com.idiotfrogs.profile.profile

import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

): BaseViewModel<ProfileAction>() {
    private val _event = MutableSharedFlow<ProfileEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.NavigateToEditProfile -> navigateToEditProfile()
            ProfileAction.NavigateToSetting -> navigateToSetting()
            ProfileAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun navigateToSetting() {
        safeLaunch { _event.emit(ProfileEvent.NavigateToSetting) }
    }

    private fun navigateToEditProfile() {
        safeLaunch { _event.emit(ProfileEvent.NavigateToEditProfile) }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(ProfileEvent.NavigateToBack) }
    }
}

sealed interface ProfileAction {
    data object NavigateToSetting : ProfileAction
    data object NavigateToEditProfile : ProfileAction
    data object NavigateToBack : ProfileAction
}

sealed interface ProfileEvent {
    data object NavigateToSetting : ProfileEvent
    data object NavigateToEditProfile : ProfileEvent
    data object NavigateToBack : ProfileEvent
}