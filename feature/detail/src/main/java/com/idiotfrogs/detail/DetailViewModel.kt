package com.idiotfrogs.detail

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.BuryTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleUseCase
import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.datetime.LocalDateTime
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleUseCase: GetTimeCapsuleUseCase,
    private val getTimeCapsuleCollaboratorsUseCase: GetTimeCapsuleCollaboratorsUseCase,
    private val buryTimeCapsuleUseCase: BuryTimeCapsuleUseCase,
) : BaseViewModel<UiState<TimeCapsuleData>, DetailSideEffect, DetailAction>() {
    override val container: Container<UiState<TimeCapsuleData>, DetailSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                fetchDetail(capsuleId)
                RefreshSideEffect.events.collect {
                    if (it is RefreshEvent.Detail) fetchDetail(it.id)
                }
            }
        }
    )

    private fun fetchDetail(capsuleId: Long) {
        safeLaunch {
            val capsuleDeferred = async { getTimeCapsuleUseCase(capsuleId) }
            val collaboratorsDeferred = async { getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20) }

            val capsuleResult = capsuleDeferred.await()
            val collaboratorsResult = collaboratorsDeferred.await()

            val results = listOf(capsuleResult, collaboratorsResult)

            intent {
                if (results.any { it.isFailure }) {
                    reduce { UiState.Error(results.first { it.isFailure }.exceptionOrNull()?.message) }
                } else {
                    reduce {
                        UiState.Success(
                            TimeCapsuleData(
                                capsule = capsuleResult.getOrNull(),
                                collaborators = collaboratorsResult.getOrNull(),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun buryTimeCapsule(openedAt: LocalDateTime) {
        safeLaunch {
            buryTimeCapsuleUseCase(
                capsuleId = capsuleId,
                body = BuryTimeCapsuleRequest(openedAt),
            ).onSuccess { response ->
                intent {
                    reduce {
                        val currentData = (state as? UiState.Success)?.data ?: TimeCapsuleData()
                        UiState.Success(currentData.copy(capsule = response))
                    }
                }
            }.onFailure {
                intent { postSideEffect(DetailSideEffect.ShowToast) }
            }
        }
    }


    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.NavigateToFriend -> intent { postSideEffect(DetailSideEffect.NavigateToFriend(action.id)) }
            is DetailAction.NavigateToMessage -> intent { postSideEffect(DetailSideEffect.NavigateToMessage(action.id)) }
            is DetailAction.NavigateToManagement -> intent { postSideEffect(DetailSideEffect.NavigateToManagement(action.id, action.title)) }
            DetailAction.NavigateToBack -> intent { postSideEffect(DetailSideEffect.NavigateToBack) }
            is DetailAction.BuryTimeCapsule -> buryTimeCapsule(action.openedAt)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): DetailViewModel
    }
}

@Immutable
data class TimeCapsuleData(
    val capsule: TimeCapsuleResponse? = null,
    val collaborators: TimeCapsuleCollaboratorsResponse? = null,
)

sealed interface DetailAction {
    data class NavigateToFriend(val id: Long) : DetailAction
    data class NavigateToMessage(val id: Long) : DetailAction
    data class NavigateToManagement(val id: Long, val title: String) : DetailAction
    data object NavigateToBack : DetailAction
    data class BuryTimeCapsule(val openedAt: LocalDateTime) : DetailAction
}

sealed interface DetailSideEffect {
    data class NavigateToFriend(val id: Long) : DetailSideEffect
    data class NavigateToMessage(val id: Long) : DetailSideEffect
    data class NavigateToManagement(val id: Long, val title: String) : DetailSideEffect
    data object NavigateToBack : DetailSideEffect
    data object ShowToast : DetailSideEffect
}
