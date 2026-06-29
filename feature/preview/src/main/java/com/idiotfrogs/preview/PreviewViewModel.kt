package com.idiotfrogs.preview

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleContentUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponseData
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = PreviewViewModel.Factory::class)
class PreviewViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleCollaboratorsUseCase: GetTimeCapsuleCollaboratorsUseCase,
    private val getTimeCapsuleContentUseCase: GetTimeCapsuleContentUseCase,
) : BaseViewModel<PreviewUiState, PreviewSideEffect, PreviewAction>() {

    override val container: Container<PreviewUiState, PreviewSideEffect> = container(
        initialState = PreviewUiState(),
        onCreate = { fetchPreview() },
    )

    private fun fetchPreview() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        val collaboratorsDeferred = async {
            getTimeCapsuleCollaboratorsUseCase(
                capsuleId = capsuleId,
                page = 0,
                size = 50,
            )
        }
        val contentsDeferred = async { getTimeCapsuleContentUseCase(capsuleId, 0, 1) }

        val collaboratorsResult = collaboratorsDeferred.await()
        val contentsResult = contentsDeferred.await()

        intent {
            if (collaboratorsResult.isFailure || contentsResult.isFailure) {
                val errorMessage = collaboratorsResult.exceptionOrNull()?.message
                    ?: contentsResult.exceptionOrNull()?.message

                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                    )
                }
            } else {
                val contentsResponse = contentsResult.getOrNull()

                reduce {
                    state.copy(
                        data = PreviewData(
                            collaborators = collaboratorsResult.getOrNull(),
                            contents = contentsResponse?.content.orEmpty(),
                            contentPage = contentsResponse?.number ?: 0,
                            isContentLast = contentsResponse?.last ?: true,
                        ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    private fun fetchNextContentPage() = safeLaunch {
        val currentData = container.stateFlow.value.data ?: return@safeLaunch

        if (currentData.isContentLast || currentData.isContentLoadingMore) return@safeLaunch

        intent {
            reduce {
                state.copy(
                    data = currentData.copy(isContentLoadingMore = true),
                )
            }
        }

        getTimeCapsuleContentUseCase(
            timeCapsuleId = capsuleId,
            page = currentData.contentPage + 1,
            size = 1,
        ).onSuccess { response ->
            intent {
                val latestData = state.data ?: PreviewData()

                reduce {
                    state.copy(
                        data = latestData.copy(
                            contents = latestData.contents + response.content,
                            contentPage = response.number,
                            isContentLast = response.last,
                            isContentLoadingMore = false,
                        ),
                        errorMessage = null,
                    )
                }
            }
        }.onFailure { error ->
            intent {
                val latestData = state.data

                reduce {
                    state.copy(
                        data = latestData?.copy(isContentLoadingMore = false),
                        errorMessage = error.message,
                    )
                }
            }
        }
    }

    override fun onAction(action: PreviewAction) {
        when (action) {
            PreviewAction.BackClicked -> intent { postSideEffect(PreviewSideEffect.NavigateToBack) }
            PreviewAction.NextContentPageRequested -> fetchNextContentPage()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): PreviewViewModel
    }
}

@Immutable
data class PreviewData(
    val collaborators: TimeCapsuleCollaboratorsResponse? = null,
    val contents: List<TimeCapsuleContentResponseData> = emptyList(),
    val contentPage: Int = 0,
    val isContentLast: Boolean = true,
    val isContentLoadingMore: Boolean = false,
)

@Immutable
data class PreviewUiState(
    override val data: PreviewData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<PreviewData>

sealed interface PreviewAction {
    data object BackClicked : PreviewAction
    data object NextContentPageRequested : PreviewAction
}

sealed interface PreviewSideEffect {
    data object NavigateToBack : PreviewSideEffect
}
