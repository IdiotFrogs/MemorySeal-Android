package com.idiotfrogs.create

import com.idiotfrogs.domain.usecase.timecapsule.CreateTimeCapsuleUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.LocalDateTime
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createTimeCapsuleUseCase: CreateTimeCapsuleUseCase
) : BaseViewModel<CreateAction>(), ContainerHost<UiState<Unit>, CreateSideEffect> {
    override val container: Container<UiState<Unit>, CreateSideEffect> = container(UiState.Success(Unit))

    override fun onAction(action: CreateAction) {
        when (action) {
            is CreateAction.CreateTimeCapsule -> createTimeCapsule(
                action.title, action.description, action.openedAt, action.mainImage
            )
            CreateAction.NavigateToBack -> intent { postSideEffect(CreateSideEffect.NavigateToBack) }
        }
    }

    private fun createTimeCapsule(
        title: String,
        description: String?,
        openedAt: LocalDateTime,
        mainImage: File
    ) {
        safeLaunch {
            val request = TimeCapsuleCreateRequest(
                title = title,
                description = description,
                openedAt = openedAt
            )
            val result = createTimeCapsuleUseCase(request, mainImage)

            result.onSuccess { response ->
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(CreateSideEffect.NavigateToDetail(response)) }
            }.onFailure { e ->
                // TODO: 에러 처리 (토스트 등)
            }
        }
    }
}

sealed interface CreateAction {
    data class CreateTimeCapsule(
        val title: String,
        val description: String?,
        val openedAt: LocalDateTime,
        val mainImage: File
    ) : CreateAction

    data object NavigateToBack : CreateAction
}

sealed interface CreateSideEffect {
    data class NavigateToDetail(val response: TimeCapsuleCreateResponse) : CreateSideEffect
    data object NavigateToBack : CreateSideEffect
}