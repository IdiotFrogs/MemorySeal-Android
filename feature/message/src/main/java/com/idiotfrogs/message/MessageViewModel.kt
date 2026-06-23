package com.idiotfrogs.message

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.CreateTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleContentUseCase
import com.idiotfrogs.domain.usecase.timecapsule.ModifyTimeCapsuleContentUseCase
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
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
) : BaseViewModel<BaseUiState, MessageSideEffect, MessageAction>() {

    override val container: Container<BaseUiState, MessageSideEffect> = container(
        initialState = UiState.Init,
        onCreate = { fetchTimeCapsuleContent() }
    )

    private fun fetchTimeCapsuleContent() = safeLaunch {
        getMyTimeCapsuleContentUseCase(capsuleId).onSuccess {
            intent { reduce { UiState.Success(MessageData(contents = it)) } }
        }.onFailure {
            intent { reduce { UiState.Error(it.message) } }
        }
    }

    private fun createTimeCapsuleContent(
        content: String,
        files: List<File>
    ) {
        safeLaunch {
            createTimeCapsuleContentUseCase(
                timeCapsuleId = capsuleId,
                content = content,
                files = files
            ).onSuccess { response ->
                intent {
                    reduce {
                        val currentData = (state as? UiState.Success)?.data ?: MessageData()
                        UiState.Success(currentData.copy(contents = currentData.contents + response))
                    }
                }
            }.onFailure {
                intent { reduce { UiState.Error(it.message) } }
            }
        }
    }

    private fun modifyTimeCapsuleContent(
        contentId: Long,
        content: String
    ) {
        safeLaunch {
            modifyTimeCapsuleContentUseCase(
                contentId = contentId,
                content = content
            ).onSuccess { response ->
                intent {
                    reduce {
                        val currentData = (state as? UiState.Success)?.data ?: MessageData()

                        UiState.Success(
                            currentData.copy(
                                contents = currentData.contents.map { content ->
                                    if (content.contentId == response.contentId) response else content
                                }
                            )
                        )
                    }
                }
            }.onFailure {
                intent { reduce { UiState.Error(it.message) } }
            }
        }
    }

    private fun deleteTimeCapsuleContent(contentIds: List<Long>) {
        safeLaunch {
            deleteTimeCapsuleContentUseCase(contentIds).onSuccess {
                intent {
                    reduce {
                        val currentData = (state as? UiState.Success)?.data ?: MessageData()

                        UiState.Success(
                            currentData.copy(
                                contents = currentData.contents.filterNot { it.contentId in contentIds }
                            )
                        )
                    }
                }
            }.onFailure {
                intent { reduce { UiState.Error(it.message) } }
            }
        }
    }

    override fun onAction(action: MessageAction) {
        when (action) {
            MessageAction.BackClicked -> intent {
                postSideEffect(MessageSideEffect.NavigateToBack)
            }

            is MessageAction.CreateContent -> createTimeCapsuleContent(
                content = action.content,
                files = action.files
            )

            is MessageAction.ModifyContent -> modifyTimeCapsuleContent(
                contentId = action.contentId,
                content = action.content
            )

            is MessageAction.DeleteContent -> deleteTimeCapsuleContent(action.contentIds)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): MessageViewModel
    }
}

@Immutable
data class MessageData(
    val contents: List<CapsuleContentsData> = emptyList(),
)

data class MessageUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface MessageAction {
    data object BackClicked : MessageAction

    data class CreateContent(
        val content: String,
        val files: List<File>,
    ) : MessageAction

    data class ModifyContent(
        val contentId: Long,
        val content: String,
    ) : MessageAction

    data class DeleteContent(
        val contentIds: List<Long>,
    ) : MessageAction
}

sealed interface MessageSideEffect {
    data object NavigateToBack : MessageSideEffect
}
