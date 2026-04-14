package com.idiotfrogs.home

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.RequestCollaboratorUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val requestCollaboratorUseCase: RequestCollaboratorUseCase,
): BaseViewModel<UiState<HomeData>, HomeSideEffect, HomeAction>() {

    override val container: Container<UiState<HomeData>, HomeSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            fetchHome()
            RefreshSideEffect.events.collect { if (it is RefreshEvent.Home) fetchHome() }
        }
    )

    private fun fetchHome() {
        safeLaunch {
            val userDeferred = async { getMyProfileUseCase() }
            val capsulesDeferred = async { getMyTimeCapsuleUseCase() }

            val userResult = userDeferred.await()
            val capsulesResult = capsulesDeferred.await()

            val results = listOf(userResult, capsulesResult)

            intent {
                if (results.any { it.isFailure }) {
                    reduce { UiState.Error(results.first { it.isFailure }.exceptionOrNull()?.message) }
                } else {
                    reduce {
                        UiState.Success(
                            HomeData(
                                user = userResult.getOrNull(),
                                capsules = capsulesResult.getOrNull() ?: emptyMap(),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun requestCollaborator(body: PendingCollaboratorsRequest) = safeLaunch {
        requestCollaboratorUseCase(body).onSuccess {
            intent { postSideEffect(HomeSideEffect.ShowToast) }
        }.onFailure {
            // TODO 추 후 에러 핸들링 맞추기 (공동 작업자 이미 신청한 사용자라면 409)
        }
    }

    override fun onAction(action: HomeAction) {
        intent {
            when (action) {
                HomeAction.NavigateToCreate -> postSideEffect(HomeSideEffect.NavigateToCreate)
                HomeAction.NavigateToProfile -> postSideEffect(HomeSideEffect.NavigateToProfile)
                is HomeAction.NavigateToDetail -> postSideEffect(HomeSideEffect.NavigateToDetail(action.id))
                is HomeAction.RequestCollaborator -> requestCollaborator(PendingCollaboratorsRequest(action.code))
            }
        }
    }
}

@Immutable
data class HomeData(
    val user: ProfileResponse? = null,
    val capsules: Map<TimeCapsuleRole, List<MyTimeCapsuleResponse>> = emptyMap()
)

sealed interface HomeAction {
    data object NavigateToCreate : HomeAction
    data object NavigateToProfile : HomeAction
    data class NavigateToDetail(val id: Long) : HomeAction

    data class RequestCollaborator(val code: String) : HomeAction
}

sealed interface HomeSideEffect {
    data object NavigateToCreate : HomeSideEffect
    data object NavigateToProfile : HomeSideEffect
    data class NavigateToDetail(val id: Long) : HomeSideEffect

    data object ShowToast : HomeSideEffect
}