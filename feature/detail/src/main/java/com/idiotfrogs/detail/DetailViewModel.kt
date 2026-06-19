package com.idiotfrogs.detail

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.BuryTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleUseCase
import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.base.DataUiState
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
) : BaseViewModel<DetailUiState, DetailSideEffect, DetailAction>() {
    override val container: Container<DetailUiState, DetailSideEffect> = container(
        initialState = DetailUiState(),
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
            intent { reduce { state.copy(isLoading = true) } }

            val capsuleDeferred = async { getTimeCapsuleUseCase(capsuleId) }
            val collaboratorsDeferred = async { getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20) }

            val capsuleResult = capsuleDeferred.await()
            val collaboratorsResult = collaboratorsDeferred.await()

            val results = listOf(capsuleResult, collaboratorsResult)

            intent {
                if (results.any { it.isFailure }) {
                    val errorMessage = results.first { it.isFailure }.exceptionOrNull()?.message

                    reduce { state.copy(isLoading = false, errorMessage = errorMessage) }
                } else {
                    reduce {
                        state.copy(
                            data = TimeCapsuleData(
                                capsule = capsuleResult.getOrNull(),
                                collaborators = collaboratorsResult.getOrNull(),
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    private fun buryTimeCapsule(openedAt: LocalDateTime) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            buryTimeCapsuleUseCase(
                capsuleId = capsuleId,
                body = BuryTimeCapsuleRequest(openedAt),
            ).onSuccess { response ->
                intent {
                    reduce {
                        val currentData = state.data ?: TimeCapsuleData()
                        state.copy(
                            data = currentData.copy(capsule = response),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                intent {
                    reduce { state.copy(isLoading = false, errorMessage = it.message) }
                    postSideEffect(DetailSideEffect.ShowToast)
                }
            }
        }
    }


    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.MemberSectionClicked -> intent { postSideEffect(DetailSideEffect.NavigateToFriend(action.id)) }
            is DetailAction.MessageSectionClicked -> intent { postSideEffect(DetailSideEffect.NavigateToMessage(action.id)) }
            is DetailAction.ManagementClicked -> intent { postSideEffect(DetailSideEffect.NavigateToManagement(action.id, action.title)) }
            DetailAction.BackClicked -> intent { postSideEffect(DetailSideEffect.NavigateToBack) }
            is DetailAction.BuryConfirmClicked -> buryTimeCapsule(action.openedAt)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): DetailViewModel
    }
}

@Immutable
data class DetailUiState(
    override val data: TimeCapsuleData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<TimeCapsuleData>

@Immutable
data class TimeCapsuleData(
    val capsule: TimeCapsuleResponse? = null,
    val collaborators: TimeCapsuleCollaboratorsResponse? = null,
)

sealed interface DetailAction {
    data class MemberSectionClicked(val id: Long) : DetailAction
    data class MessageSectionClicked(val id: Long) : DetailAction
    data class ManagementClicked(val id: Long, val title: String) : DetailAction
    data object BackClicked : DetailAction
    data class BuryConfirmClicked(val openedAt: LocalDateTime) : DetailAction
}

sealed interface DetailSideEffect {
    data class NavigateToFriend(val id: Long) : DetailSideEffect
    data class NavigateToMessage(val id: Long) : DetailSideEffect
    data class NavigateToManagement(val id: Long, val title: String) : DetailSideEffect
    data object NavigateToBack : DetailSideEffect
    data object ShowToast : DetailSideEffect
}
