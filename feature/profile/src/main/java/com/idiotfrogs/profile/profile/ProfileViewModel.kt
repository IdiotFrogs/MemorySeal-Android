package com.idiotfrogs.profile.profile

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.auth.LogoutUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.domain.usecase.user.WithdrawUseCase
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
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
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase
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

    private fun logout() {
        safeLaunch {
            logoutUseCase.invoke()
                .onSuccess {
                    intent {
                        postSideEffect(ProfileSideEffect.NavigateToLogin)
                    }
                }
                .onFailure { /** no-op */ }
        }
    }

    private fun withdraw() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            withdrawUseCase()
                .onSuccess {
                    intent {
                        reduce { state.copy(isLoading = false, errorMessage = null) }
                        postSideEffect(ProfileSideEffect.NavigateToLogin)
                    }
                }.onFailure {
                    intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                }
        }
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LogoutConfirmed -> logout()
            ProfileAction.WithdrawConfirmed -> withdraw()
            ProfileAction.EditProfileClicked -> intent { postSideEffect(ProfileSideEffect.NavigateToEditProfile) }
            ProfileAction.BackClicked -> intent { postSideEffect(ProfileSideEffect.NavigateToBack) }
            is ProfileAction.TicketClicked -> intent { postSideEffect(ProfileSideEffect.NavigateToDetail(action.id))}
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
    data object EditProfileClicked : ProfileAction
    data object BackClicked : ProfileAction
    data class TicketClicked(val id: Long) : ProfileAction
    data object LogoutConfirmed : ProfileAction
    data object WithdrawConfirmed : ProfileAction
}

sealed interface ProfileSideEffect {
    data object NavigateToEditProfile : ProfileSideEffect
    data object NavigateToBack : ProfileSideEffect
    data class NavigateToDetail(val id: Long) : ProfileSideEffect
    data object NavigateToLogin : ProfileSideEffect
}
