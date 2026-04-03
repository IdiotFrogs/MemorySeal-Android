package com.idiotfrogs.profile.profile

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

) : BaseViewModel<UiState<Unit>, ProfileSideEffect, ProfileAction>() {

    override val container: Container<UiState<Unit>, ProfileSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                // TODO 초기 데이터 로딩
                intent { reduce { UiState.Success(Unit) } }

                RefreshSideEffect.events.collect {
                    if (it is RefreshEvent.Profile) {
                        // TODO fetchProfile()
                    }
                }
            }
        }
    )

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.NavigateToEditProfile -> intent { postSideEffect(ProfileSideEffect.NavigateToEditProfile) }
            ProfileAction.NavigateToSetting -> intent { postSideEffect(ProfileSideEffect.NavigateToSetting) }
            ProfileAction.NavigateToBack -> intent { postSideEffect(ProfileSideEffect.NavigateToBack) }
        }
    }
}

sealed interface ProfileAction {
    data object NavigateToSetting : ProfileAction
    data object NavigateToEditProfile : ProfileAction
    data object NavigateToBack : ProfileAction
}

sealed interface ProfileSideEffect {
    data object NavigateToSetting : ProfileSideEffect
    data object NavigateToEditProfile : ProfileSideEffect
    data object NavigateToBack : ProfileSideEffect
}