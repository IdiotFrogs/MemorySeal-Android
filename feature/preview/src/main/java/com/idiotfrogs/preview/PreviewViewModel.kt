package com.idiotfrogs.preview

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleContentUseCase
import com.idiotfrogs.model.timecapsule.MyCapsuleContentsData
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = PreviewViewModel.Factory::class)
class PreviewViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getMyTimeCapsuleContentUseCase: GetMyTimeCapsuleContentUseCase,
) : BaseViewModel<PreviewUiState, PreviewSideEffect, PreviewAction>() {

    override val container: Container<PreviewUiState, PreviewSideEffect> = container(
        initialState = PreviewUiState(),
        onCreate = { fetchPreview() },
    )

    private fun fetchPreview() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        getMyTimeCapsuleContentUseCase(capsuleId)
            .onSuccess { contents ->
                intent {
                    reduce {
                        state.copy(
                            data = PreviewData(contents = contents),
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

    override fun onAction(action: PreviewAction) {
        when (action) {
            PreviewAction.BackClicked -> intent { postSideEffect(PreviewSideEffect.NavigateToBack) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): PreviewViewModel
    }
}

@Immutable
data class PreviewData(
    val contents: List<MyCapsuleContentsData> = emptyList(),
)

@Immutable
data class PreviewUiState(
    override val data: PreviewData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<PreviewData>

sealed interface PreviewAction {
    data object BackClicked : PreviewAction
}

sealed interface PreviewSideEffect {
    data object NavigateToBack : PreviewSideEffect
}
