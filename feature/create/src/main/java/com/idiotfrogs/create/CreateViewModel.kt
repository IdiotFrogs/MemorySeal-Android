package com.idiotfrogs.create

import com.idiotfrogs.domain.usecase.timecapsule.CreateTimeCapsuleUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.LocalDateTime
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createTimeCapsuleUseCase: CreateTimeCapsuleUseCase
): BaseViewModel<CreateAction>() {
    private val _event = MutableSharedFlow<CreateEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: CreateAction) {
        when (action) {
            is CreateAction.CreateTimeCapsule -> createTimeCapsule(
                action.title, action.description, action.openedAt, action.mainImage
            )
            CreateAction.NavigateToBack -> navigateToBack()
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
            val response = createTimeCapsuleUseCase(request, mainImage)
            _event.emit(CreateEvent.Success(response))
        }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(CreateEvent.NavigateToBack) }
    }
}

sealed interface CreateAction {
    data class CreateTimeCapsule(
        val title: String,
        val description: String?,
        val openedAt: LocalDateTime,
        val mainImage: File
    ): CreateAction

    data object NavigateToBack : CreateAction
}

sealed interface CreateEvent {
    data class Success(val response: TimeCapsuleCreateResponse) : CreateEvent
    data object NavigateToBack : CreateEvent
}