package com.idiotfrogs.detail

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleUseCase
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
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleUseCase: GetTimeCapsuleUseCase,
    private val getTimeCapsuleCollaboratorsUseCase: GetTimeCapsuleCollaboratorsUseCase,
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
            val collaboratorsDeferred = async { getTimeCapsuleCollaboratorsUseCase(capsuleId) }

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
                                collaborators = collaboratorsResult.getOrNull() ?: emptyList(),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun sealVote(agree: Boolean) {
        // TODO 투표 api 연결 필요
    }

    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.NavigateToFriend -> intent { postSideEffect(DetailSideEffect.NavigateToFriend(action.id)) }
            is DetailAction.NavigateToManagement -> intent { postSideEffect(DetailSideEffect.NavigateToManagement(action.id, action.title)) }
            DetailAction.NavigateToBack -> intent { postSideEffect(DetailSideEffect.NavigateToBack) }
            is DetailAction.SealVote -> sealVote(action.agree)
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
    val collaborators: List<TimeCapsuleCollaboratorsResponse> = emptyList()
)

sealed interface DetailAction {
    data class NavigateToFriend(val id: Long) : DetailAction
    data class NavigateToManagement(val id: Long, val title: String) : DetailAction
    data object NavigateToBack : DetailAction

    data class SealVote(val agree: Boolean) : DetailAction
}

sealed interface DetailSideEffect {
    data class NavigateToFriend(val id: Long) : DetailSideEffect
    data class NavigateToManagement(val id: Long, val title: String) : DetailSideEffect
    data object NavigateToBack : DetailSideEffect
}
