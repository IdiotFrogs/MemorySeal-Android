package com.idiotfrogs.memory

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleContentUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponse
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.paging.PaginationState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

private const val MEMORY_COLLABORATOR_PAGE_SIZE = 20
private const val MEMORY_CONTENT_PAGE_SIZE = 1

@HiltViewModel(assistedFactory = MemoryViewModel.Factory::class)
class MemoryViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleCollaboratorsUseCase: GetTimeCapsuleCollaboratorsUseCase,
    private val getTimeCapsuleContentUseCase: GetTimeCapsuleContentUseCase,
) : BaseViewModel<MemoryUiState, MemorySideEffect, MemoryAction>() {

    override val container: Container<MemoryUiState, MemorySideEffect> = container(
        initialState = MemoryUiState(),
        onCreate = { fetchMemory() },
    )

    private fun fetchMemory() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        val collaboratorsDeferred = async {
            getTimeCapsuleCollaboratorsUseCase(
                capsuleId = capsuleId,
                page = 0,
                size = MEMORY_COLLABORATOR_PAGE_SIZE,
            )
        }
        val memoryContentsDeferred = async {
            getTimeCapsuleContentUseCase(
                timeCapsuleId = capsuleId,
                page = 0,
                size = MEMORY_CONTENT_PAGE_SIZE,
            )
        }

        val collaboratorsResult = collaboratorsDeferred.await()
        val memoryContentsResult = memoryContentsDeferred.await()

        intent {
            if (collaboratorsResult.isFailure || memoryContentsResult.isFailure) {
                val errorMessage = collaboratorsResult.exceptionOrNull()?.message
                    ?: memoryContentsResult.exceptionOrNull()?.message

                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                    )
                }
            } else {
                val collaboratorsResponse = collaboratorsResult.getOrThrow()
                val memoryContentsResponse = memoryContentsResult.getOrThrow()

                reduce {
                    state.copy(
                        data = MemoryData(
                            collaborators = PaginationState<MemoryCollaboratorUiModel>()
                                .addPage(collaboratorsResponse),
                            memoryContents = PaginationState<MemoryContentUiModel>()
                                .addPage(memoryContentsResponse),
                        ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    private fun fetchNextCollaboratorsPage() = intent {
        val currentData = state.data ?: return@intent
        val collaborators = currentData.collaborators

        if (state.isLoading || !collaborators.canLoadMore) return@intent

        val currentPage = collaborators.currentPage
        val nextPage = currentPage + 1

        reduce {
            state.copy(
                data = currentData.copy(
                    collaborators = collaborators.setLoadingMore(true),
                ),
            )
        }

        getTimeCapsuleCollaboratorsUseCase(
            capsuleId = capsuleId,
            page = nextPage,
            size = MEMORY_COLLABORATOR_PAGE_SIZE,
        ).onSuccess { response ->
            val latestData = state.data ?: return@onSuccess
            val latestCollaborators = latestData.collaborators
            val isCurrentRequest = latestCollaborators.currentPage == currentPage &&
                latestCollaborators.isLoadingMore

            if (!isCurrentRequest) return@onSuccess

            reduce {
                state.copy(
                    data = latestData.copy(
                        collaborators = latestCollaborators.addPage(response),
                    ),
                    errorMessage = null,
                )
            }
        }.onFailure { error ->
            val latestData = state.data ?: return@onFailure
            val latestCollaborators = latestData.collaborators
            val isCurrentRequest = latestCollaborators.currentPage == currentPage &&
                latestCollaborators.isLoadingMore

            if (!isCurrentRequest) return@onFailure

            reduce {
                state.copy(
                    data = latestData.copy(
                        collaborators = latestCollaborators.setLoadingMore(false),
                    ),
                    errorMessage = error.message,
                )
            }
        }
    }

    private fun fetchNextMemoryContentPage() = intent {
        val currentData = state.data ?: return@intent
        val memoryContents = currentData.memoryContents

        if (
            state.isLoading ||
            currentData.selectedCollaboratorIndex != null ||
            !memoryContents.canLoadMore
        ) {
            return@intent
        }

        val nextPage = memoryContents.nextMissingPage()

        if (nextPage == null) {
            reduce {
                state.copy(
                    data = currentData.copy(
                        memoryContents = memoryContents.copy(
                            currentPage = (memoryContents.totalElements - 1)
                                .toInt()
                                .coerceAtLeast(memoryContents.currentPage),
                            isLast = true,
                        ),
                    ),
                )
            }
            return@intent
        }

        val advancedCurrentPage = nextPage - 1

        reduce {
            state.copy(
                data = currentData.copy(
                    memoryContents = memoryContents.copy(
                        currentPage = advancedCurrentPage,
                        isLoadingMore = true,
                    ),
                ),
            )
        }

        getTimeCapsuleContentUseCase(
            timeCapsuleId = capsuleId,
            page = nextPage,
            size = MEMORY_CONTENT_PAGE_SIZE,
        ).onSuccess { response ->
            val latestData = state.data ?: return@onSuccess
            val latestContents = latestData.memoryContents
            val isCurrentRequest = latestContents.currentPage == advancedCurrentPage &&
                latestContents.isLoadingMore

            if (!isCurrentRequest) return@onSuccess

            reduce {
                state.copy(
                    data = latestData.copy(
                        memoryContents = latestContents.addPage(response),
                    ),
                    errorMessage = null,
                )
            }
        }.onFailure { error ->
            val latestData = state.data ?: return@onFailure
            val latestContents = latestData.memoryContents
            val isCurrentRequest = latestContents.currentPage == advancedCurrentPage &&
                latestContents.isLoadingMore

            if (!isCurrentRequest) return@onFailure

            reduce {
                state.copy(
                    data = latestData.copy(
                        memoryContents = latestContents.setLoadingMore(false),
                    ),
                    errorMessage = error.message,
                )
            }
        }
    }

    private fun selectCollaborator(collaboratorIndex: Int) = intent {
        val currentData = state.data ?: return@intent
        currentData.collaborators.items.getOrNull(collaboratorIndex) ?: return@intent
        val nextSelectedIndex = collaboratorIndex.takeUnless {
            it == currentData.selectedCollaboratorIndex
        }

        reduce {
            state.copy(
                data = currentData.copy(selectedCollaboratorIndex = nextSelectedIndex),
            )
        }

        val shouldFetchMemoryContent = nextSelectedIndex != null &&
            currentData.memoryContents.items.none {
                it.collaboratorIndex == collaboratorIndex
            } &&
            !(
                currentData.memoryContents.isLoadingMore &&
                    collaboratorIndex == currentData.memoryContents.currentPage + 1
            )

        if (shouldFetchMemoryContent) {
            fetchCollaboratorMemoryContent(collaboratorIndex)
        }
    }

    private fun fetchCollaboratorMemoryContent(collaboratorIndex: Int) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        getTimeCapsuleContentUseCase(
            timeCapsuleId = capsuleId,
            page = collaboratorIndex,
            size = MEMORY_CONTENT_PAGE_SIZE,
        ).onSuccess { response ->
            intent {
                val latestData = state.data ?: MemoryData()

                reduce {
                    state.copy(
                        data = latestData.copy(
                            memoryContents = latestData.memoryContents
                                .mergeSelectedContent(response),
                        ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure { error ->
            intent {
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
            }
        }
    }

    override fun onAction(action: MemoryAction) {
        when (action) {
            MemoryAction.BackClicked -> intent { postSideEffect(MemorySideEffect.NavigateToBack) }
            is MemoryAction.CollaboratorClicked -> selectCollaborator(action.index)
            MemoryAction.NextCollaboratorsPageRequested -> fetchNextCollaboratorsPage()
            MemoryAction.NextMemoryContentPageRequested -> fetchNextMemoryContentPage()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): MemoryViewModel
    }
}

@Immutable
data class MemoryData(
    val collaborators: PaginationState<MemoryCollaboratorUiModel> = PaginationState(),
    val memoryContents: PaginationState<MemoryContentUiModel> = PaginationState(),
    val selectedCollaboratorIndex: Int? = null,
)

@Immutable
data class MemoryUiState(
    override val data: MemoryData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<MemoryData>

sealed interface MemoryAction {
    data object BackClicked : MemoryAction
    data class CollaboratorClicked(val index: Int) : MemoryAction
    data object NextCollaboratorsPageRequested : MemoryAction
    data object NextMemoryContentPageRequested : MemoryAction
}

sealed interface MemorySideEffect {
    data object NavigateToBack : MemorySideEffect
}

internal fun PaginationState<MemoryCollaboratorUiModel>.addPage(
    response: TimeCapsuleCollaboratorsResponse,
): PaginationState<MemoryCollaboratorUiModel> = addPage(
    newItems = response.content.map { it.toUiModel() },
    page = response.number,
    totalElements = response.totalElements.toLong(),
    isLast = response.last,
)

internal fun PaginationState<MemoryContentUiModel>.addPage(
    response: TimeCapsuleContentResponse,
): PaginationState<MemoryContentUiModel> {
    val updatedState = addPage(
        newItems = response.content.map {
            it.toUiModel(collaboratorIndex = response.number)
        },
        page = response.number,
        totalElements = response.totalElements.toLong(),
        isLast = response.last,
    )

    return updatedState.copy(items = updatedState.items.normalized())
}

internal fun PaginationState<MemoryContentUiModel>.mergeSelectedContent(
    response: TimeCapsuleContentResponse,
): PaginationState<MemoryContentUiModel> {
    val incoming = response.content.map {
        it.toUiModel(collaboratorIndex = response.number)
    }

    return copy(items = (items + incoming).normalized())
}

internal fun PaginationState<MemoryContentUiModel>.nextMissingPage(): Int? {
    var nextPage = currentPage + 1

    while (
        nextPage.toLong() < totalElements &&
        items.any { it.collaboratorIndex == nextPage }
    ) {
        nextPage++
    }

    return nextPage.takeIf { it.toLong() < totalElements }
}

private fun List<MemoryContentUiModel>.normalized(): List<MemoryContentUiModel> =
    distinctBy { it.collaboratorIndex }
        .sortedBy { it.collaboratorIndex }
