package com.idiotfrogs.profile.profile

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase
) : BaseViewModel<ProfileUiState, ProfileSideEffect, ProfileAction>() {

    override val container: Container<ProfileUiState, ProfileSideEffect> = container(
        initialState = ProfileUiState(),
        onCreate = {
            safeLaunch {
                fetchProfile()
                RefreshSideEffect.events.collect {
                    if (it is RefreshEvent.Profile) {
                        fetchProfile()
                    }
                }
            }
        }
    )

    private fun fetchProfile() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            val userDeferred = async { getMyProfileUseCase() }
            val capsulesDeferred = async { getMyTimeCapsuleUseCase() }

            val userResult = userDeferred.await()
            val capsulesResult = capsulesDeferred.await()

            val results = listOf(userResult, capsulesResult)

            intent {
                if (results.any { it.isFailure }) {
                    val errorMessage = results.first { it.isFailure }.exceptionOrNull()?.message

                    reduce { state.copy(isLoading = false, errorMessage = errorMessage) }
                } else {
                    reduce {
                        state.copy(
                            data = ProfileData(
                                user = userResult.getOrNull(),
                                capsules = capsulesResult.getOrNull()
                                    ?.flatMap { it.value }
                                    ?.filter { it.timeCapsuleStatus == TimeCapsuleStatus.OPENED }
                                    ?: emptyList(),
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.NavigateToEditProfile -> intent { postSideEffect(ProfileSideEffect.NavigateToEditProfile) }
            ProfileAction.NavigateToSetting -> intent { postSideEffect(ProfileSideEffect.NavigateToSetting) }
            ProfileAction.NavigateToBack -> intent { postSideEffect(ProfileSideEffect.NavigateToBack) }
        }
    }
}

@Immutable
data class ProfileUiState(
    override val data: ProfileData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<ProfileData>

@Immutable
data class ProfileData(
    val user: ProfileResponse? = null,
    val capsules: List<MyTimeCapsuleResponse> = emptyList()
)

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
