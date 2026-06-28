package com.idiotfrogs.profile.editprofile

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.domain.usecase.user.UpdateMyProfileUseCase
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateMyProfileUseCase: UpdateMyProfileUseCase,
) : BaseViewModel<EditProfileUiState, EditProfileSideEffect, EditProfileAction>() {

    override val container: Container<EditProfileUiState, EditProfileSideEffect> = container(
        initialState = EditProfileUiState(),
        onCreate = {
            safeLaunch {
                fetchProfile()
            }
        }
    )

    override fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.BackClicked -> intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
            is EditProfileAction.UpdateProfile -> {
                updateProfile(action.userId, action.profileImage, action.nickname)
            }
        }
    }

    private fun fetchProfile() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }
            val result = getMyProfileUseCase()

            intent {
                if (result.isFailure) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = (result.exceptionOrNull()?.message)
                        )
                    }
                } else {
                    reduce {
                        state.copy(
                            isLoading = false,
                            data = EditProfileData(
                                result.getOrNull()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun updateProfile(userId: Long, profileImage: File?, nickname: String) {
        safeLaunch {
            updateMyProfileUseCase(
                userId = userId,
                profileImage = profileImage,
                nickname = nickname
            )
                .onSuccess {
                    RefreshSideEffect.tryEmit(RefreshEvent.Profile)
                    intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
                }
        }
    }
}

@Immutable
data class EditProfileUiState(
    override val data: EditProfileData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<EditProfileData>

@Immutable
data class EditProfileData(
    val user: ProfileResponse? = null
)

sealed interface EditProfileAction {
    data object BackClicked : EditProfileAction
    data class UpdateProfile(
        val userId: Long, val profileImage: File?, val nickname: String
    ) : EditProfileAction
}

sealed interface EditProfileSideEffect {
    data object NavigateToBack : EditProfileSideEffect
}