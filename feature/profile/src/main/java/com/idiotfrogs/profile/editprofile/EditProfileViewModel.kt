package com.idiotfrogs.profile.editprofile

import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(

) : BaseViewModel<EditProfileUiState, EditProfileSideEffect, EditProfileAction>() {

    override val container: Container<EditProfileUiState, EditProfileSideEffect> = container(
        initialState = EditProfileUiState(),
        onCreate = {
            // TODO 초기 데이터 로딩 or 네비게이션에서 response 받아오기
            safeLaunch {
                intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
            }
        }
    )

    override fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.BackClicked -> intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
            EditProfileAction.SaveClicked -> intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
        }
    }
}

data class EditProfileUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface EditProfileAction {
    data object BackClicked : EditProfileAction
    data object SaveClicked : EditProfileAction
}

sealed interface EditProfileSideEffect {
    data object NavigateToBack : EditProfileSideEffect
}
