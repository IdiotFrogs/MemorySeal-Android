package com.idiotfrogs.profile.editprofile

import com.idiotfrogs.util.base.BaseUiState
import android.util.Log
import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.domain.usecase.user.UpdateMyProfileUseCase
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import com.idiotfrogs.profile.profile.ProfileData
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateMyProfileUseCase: UpdateMyProfileUseCase,
) : BaseViewModel<EditProfileUiState, EditProfileSideEffect, EditProfileAction>() {

    override val container: Container<EditProfileData, EditProfileSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                fetchProfile()
            }
        }
    )

    override fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.NavigateToBack -> intent { postSideEffect(EditProfileSideEffect.NavigateToBack) }
            is EditProfileAction.UpdateProfile -> {
                updateProfile(action.userId, action.profileImage, action.nickname)
            }
        }
    }

    private fun fetchProfile() {
        safeLaunch {
            intent { reduce { UiState.} }
            val result = getMyProfileUseCase()

            intent {
                if (result.isFailure) {
                    reduce { UiState.Error(result.exceptionOrNull()?.message) }
                } else {
                    reduce {
                        UiState.Success(
                            EditProfileData(user = result.getOrNull())
                        )
                    }
                }
            }
        }

        private fun updateMyProfile(userId: Long, profileImage: File?, nickname: String) {
            safeLaunch {
                updateMyProfileUseCase(
                    userId = userId,
                    profileImage = profileImage,
                    nickname = nickname
                )
                    .onSuccess {
                        RefreshSideEffect.tryEmit(RefreshEvent.Profile)
                        intent { postSideEffect(com.idiotfrogs.profile.editprofile.EditProfileSideEffect.NavigateToBack) }
                    }
                    .onFailure {
                        Log.d("TAG", "updateMyProfile: ${it.message}")
                    }
            }
        }
    }

data class EditProfileUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface EditProfileAction {
    data object BackClicked : EditProfileAction
    data class UpdateProfile(
        val userId: Long, val profileImage: File?, val nickname: String
    ): EditProfileAction
}

sealed interface EditProfileSideEffect {
    data object NavigateToBack : EditProfileSideEffect
}