package com.idiotfrogs.home

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.auth.PutFcmTokenUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetViewedTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.RequestCollaboratorUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.notification.FcmTokenProvider
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val requestCollaboratorUseCase: RequestCollaboratorUseCase,
    private val fcmTokenProvider: FcmTokenProvider,
    private val putFcmTokenUseCase: PutFcmTokenUseCase,
    private val getViewedTimeCapsuleUseCase: GetViewedTimeCapsuleUseCase,
) : BaseViewModel<HomeUiState, HomeSideEffect, HomeAction>() {

    override val container: Container<HomeUiState, HomeSideEffect> = container(
        initialState = HomeUiState(),
        onCreate = {
            fetchHome()
            syncFcmToken()
            RefreshSideEffect.events.collect { if (it is RefreshEvent.Home) fetchHome() }
        }
    )

    private fun syncFcmToken() {
        safeLaunch {
            val fcmToken = fcmTokenProvider.getToken()
            putFcmTokenUseCase(fcmToken).onFailure {
                // TODO 로그 남기기 (Firebase)
            }
        }
    }

    private fun fetchHome() {
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
                            data = HomeData(
                                user = userResult.getOrNull(),
                                capsules = capsulesResult.getOrNull() ?: emptyMap(),
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    private fun requestCollaborator(body: PendingCollaboratorsRequest) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        requestCollaboratorUseCase(body).onSuccess {
            intent {
                reduce { state.copy(isLoading = false, errorMessage = null) }
                postSideEffect(HomeSideEffect.ShowToast)
                fetchHome()
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            // TODO 추 후 에러 핸들링 맞추기 (공동 작업자 이미 신청한 사용자라면 409)
        }
    }

    private fun checkTimeCapsule(capsuleId: Long, timeCapsuleStatus: TimeCapsuleStatus) = safeLaunch {
        val capsuleIds = getViewedTimeCapsuleUseCase.capsuleIds.first()

        // 만약 티켓이 오픈된 이후 처음 확인하는 거라면
        if (capsuleId !in capsuleIds && timeCapsuleStatus == TimeCapsuleStatus.OPENED) {
            intent { postSideEffect(HomeSideEffect.ShowOpenAnimation) }
        } else {
            intent { postSideEffect(HomeSideEffect.NavigateToDetail(capsuleId)) }
        }
    }

    override fun onAction(action: HomeAction) {
        intent {
            when (action) {
                HomeAction.CreateClicked -> postSideEffect(HomeSideEffect.NavigateToCreate)
                HomeAction.ProfileClicked -> postSideEffect(HomeSideEffect.NavigateToProfile)
                is HomeAction.TimeCapsuleClicked -> checkTimeCapsule(action.id, action.timeCapsuleStatus)
                is HomeAction.JoinCodeSubmitted -> requestCollaborator(PendingCollaboratorsRequest(action.code))
                HomeAction.Refresh -> fetchHome()
            }
        }
    }
}

@Immutable
data class HomeUiState(
    override val data: HomeData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<HomeData>

@Immutable
data class HomeData(
    val user: ProfileResponse? = null,
    val capsules: Map<TimeCapsuleRole, List<MyTimeCapsuleResponse>> = emptyMap()
)

sealed interface HomeAction {
    data object CreateClicked : HomeAction
    data object ProfileClicked : HomeAction
    data class TimeCapsuleClicked(val id: Long, val timeCapsuleStatus: TimeCapsuleStatus) : HomeAction
    data class JoinCodeSubmitted(val code: String) : HomeAction
    data object Refresh : HomeAction
}

sealed interface HomeSideEffect {
    data object NavigateToCreate : HomeSideEffect
    data object NavigateToProfile : HomeSideEffect
    data class NavigateToDetail(val id: Long) : HomeSideEffect

    data object ShowToast : HomeSideEffect
    data object ShowOpenAnimation : HomeSideEffect
}
