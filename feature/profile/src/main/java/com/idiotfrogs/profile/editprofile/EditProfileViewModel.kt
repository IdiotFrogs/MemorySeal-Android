package com.idiotfrogs.profile.editprofile

import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(

): BaseViewModel<EditProfileAction>() {
    private val _event = MutableSharedFlow<EditProfileEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(EditProfileEvent.NavigateToBack) }
    }
}

sealed interface EditProfileAction {
    data object NavigateToBack : EditProfileAction
}

sealed interface EditProfileEvent {
    data object NavigateToBack : EditProfileEvent
}