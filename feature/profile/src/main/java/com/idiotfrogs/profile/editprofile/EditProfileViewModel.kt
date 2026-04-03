package com.idiotfrogs.profile.editprofile

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(

) : BaseViewModel<UiState<Unit>, EditProfileSideEffect, EditProfileAction>() {

    override val container: Container<UiState<Unit>, EditProfileSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            // TODO 초기 데이터 로딩 or 네비게이션에서 response 받아오기
            safeLaunch {
                intent { reduce { UiState.Success(Unit) } }
            }
        }
    )

    override fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.NavigateToBack -> intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
        }
    }
}

sealed interface EditProfileAction {
    data object NavigateToBack : EditProfileAction
}

sealed interface EditProfileSideEffect {
    data object NavigateToBack : EditProfileSideEffect
}