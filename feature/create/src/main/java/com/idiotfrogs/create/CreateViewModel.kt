package com.idiotfrogs.create

import com.idiotfrogs.domain.usecase.timecapsule.CreateTimeCapsuleUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createTimeCapsuleUseCase: CreateTimeCapsuleUseCase
) : BaseViewModel<CreateUiState, CreateSideEffect, CreateAction>() {
    override val container: Container<CreateUiState, CreateSideEffect> = container(CreateUiState())

    override fun onAction(action: CreateAction) {
        when (action) {
            is CreateAction.CreateButtonClicked -> createTimeCapsule(
                action.title, action.description, action.mainImage
            )
            CreateAction.BackClicked -> intent { postSideEffect(CreateSideEffect.NavigateToBack) }
        }
    }

    private fun createTimeCapsule(
        title: String,
        description: String?,
        mainImage: File
    ) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            val request = TimeCapsuleCreateRequest(
                title = title,
                description = description,
            )
            val result = createTimeCapsuleUseCase(request, mainImage)

            result.onSuccess { response ->
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(CreateSideEffect.NavigateToDetail(response)) }
            }.onFailure { e ->
                intent { reduce { state.copy(isLoading = false, errorMessage = e.message) } }
                // TODO: 에러 처리 (토스트 등)
            }
        }
    }
}

data class CreateUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface CreateAction {
    data class CreateButtonClicked(
        val title: String,
        val description: String?,
        val mainImage: File
    ) : CreateAction
    data object BackClicked : CreateAction
}

sealed interface CreateSideEffect {
    data class NavigateToDetail(val response: TimeCapsuleCreateResponse) : CreateSideEffect
    data object NavigateToBack : CreateSideEffect
}
