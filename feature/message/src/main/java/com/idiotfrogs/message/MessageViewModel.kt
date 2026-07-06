package com.idiotfrogs.message

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.CreateTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.ModifyTimeCapsuleContentUseCase
import com.idiotfrogs.model.timecapsule.MyCapsuleContentsData
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.io.File

@HiltViewModel(assistedFactory = MessageViewModel.Factory::class)
class MessageViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getMyTimeCapsuleContentUseCase: GetMyTimeCapsuleContentUseCase,
    private val createTimeCapsuleContentUseCase: CreateTimeCapsuleContentUseCase,
    private val modifyTimeCapsuleContentUseCase: ModifyTimeCapsuleContentUseCase,
    private val deleteTimeCapsuleContentUseCase: DeleteTimeCapsuleContentUseCase,
) : BaseViewModel<MessageUiState, MessageSideEffect, MessageAction>() {

    override val container: Container<MessageUiState, MessageSideEffect> = container(
        initialState = MessageUiState(),
        onCreate = { fetchTimeCapsuleContent() }
    )

    private fun fetchTimeCapsuleContent() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        getMyTimeCapsuleContentUseCase(capsuleId).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = MessageData(contents = it),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
        }
    }

    private fun createTimeCapsuleContent(
        content: String,
        files: List<File>
    ) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            createTimeCapsuleContentUseCase(
                timeCapsuleId = capsuleId,
                content = content,
                files = files
            ).onSuccess {
                getMyTimeCapsuleContentUseCase(capsuleId).onSuccess { contents ->
                    intent {
                        reduce {
                            state.copy(
                                data = MessageData(contents = contents),
                                isLoading = false,
                                errorMessage = null,
                            )
                        }
                    }
                }.onFailure {
                    intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                }
                RefreshSideEffect.tryEmit(RefreshEvent.Detail(capsuleId))
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            }
        }
    }

    private fun modifyTimeCapsuleContent(
        contentId: Long,
        content: String
    ) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            modifyTimeCapsuleContentUseCase(
                contentId = contentId,
                content = content
            ).onSuccess { response ->
                intent {
                    reduce {
                        val currentData = state.data ?: MessageData()

                        state.copy(
                            data = currentData.copy(
                                contents = currentData.contents.map { content ->
                                    if (content.contentId == response.contentId) {
                                        content.copy(content = response.content)
                                    } else {
                                        content
                                    }
                                }
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            }
        }
    }

    private fun deleteTimeCapsuleContent(
        contentIds: List<Long>,
        fileIds: List<Long>,
    ) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            deleteTimeCapsuleContentUseCase(contentIds, fileIds).onSuccess {
                intent {
                    reduce {
                        val currentData = state.data ?: MessageData()

                        state.copy(
                            data = currentData.copy(
                                contents = currentData.contents.mapNotNull { content ->
                                    if (fileIds.isEmpty() && content.contentId in contentIds) {
                                        return@mapNotNull null
                                    }

                                    val remainingFiles = content.attachedFiles
                                        ?.filterNot { it.id in fileIds }

                                    if (content.content.isNullOrBlank() && remainingFiles.isNullOrEmpty()) {
                                        null
                                    } else {
                                        content.copy(attachedFiles = remainingFiles)
                                    }
                                }
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                RefreshSideEffect.tryEmit(RefreshEvent.Detail(capsuleId))
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            }
        }
    }

    override fun onAction(action: MessageAction) {
        when (action) {
            MessageAction.BackClicked -> intent {
                postSideEffect(MessageSideEffect.NavigateToBack)
            }

            MessageAction.PreviewClicked -> intent {
                postSideEffect(MessageSideEffect.NavigateToPreview(capsuleId))
            }

            is MessageAction.ContentCreateSubmitted -> createTimeCapsuleContent(
                content = action.content,
                files = action.files
            )

            is MessageAction.ContentModifySubmitted -> modifyTimeCapsuleContent(
                contentId = action.contentId,
                content = action.content
            )

            is MessageAction.ContentDeleteConfirmed -> deleteTimeCapsuleContent(
                contentIds = action.contentIds,
                fileIds = action.fileIds
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): MessageViewModel
    }
}

@Immutable
data class MessageData(
    val contents: List<MyCapsuleContentsData> = emptyList(),
)

@Immutable
data class MessageUiState(
    override val data: MessageData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<MessageData>

sealed interface MessageAction {
    data object BackClicked : MessageAction
    data object PreviewClicked : MessageAction

    data class ContentCreateSubmitted(
        val content: String,
        val files: List<File>,
    ) : MessageAction

    data class ContentModifySubmitted(
        val contentId: Long,
        val content: String,
    ) : MessageAction

    data class ContentDeleteConfirmed(
        val contentIds: List<Long>,
        val fileIds: List<Long>,
    ) : MessageAction
}

sealed interface MessageSideEffect {
    data object NavigateToBack : MessageSideEffect
    data class NavigateToPreview(val id: Long) : MessageSideEffect
}
