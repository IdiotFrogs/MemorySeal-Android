package com.idiotfrogs.open

import androidx.compose.runtime.Immutable
import com.idiotfrogs.analytics.AnalyticsTracker
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleUseCase
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import kotlin.time.Clock

@HiltViewModel(assistedFactory = OpenViewModel.Factory::class)
class OpenViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleUseCase: GetTimeCapsuleUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<OpenUiState, OpenSideEffect, OpenAction>() {
    override val container: Container<OpenUiState, OpenSideEffect> = container(
        initialState = OpenUiState(),
        onCreate = { fetchOpen() }
    )

    override fun onAction(action: OpenAction) {
        when (action) {
            OpenAction.DoneClick -> intent { postSideEffect(OpenSideEffect.NavigateToMemory(capsuleId)) }
        }
    }

    fun fetchOpen() = intent {
        getTimeCapsuleUseCase.invoke(capsuleId)
            .onSuccess { capsule ->
                capsule.openedAt?.let { openedAt ->
                    val viewedAt = Clock.System.todayIn(TimeZone.of("Asia/Seoul"))
                    analyticsTracker.ticketOpened(
                        viewedAt.toEpochDays() - openedAt.toEpochDays()
                    )
                }
                reduce { state.copy(data = OpenData(imageUrl = capsule.mainImageUrl)) }
            }
            .onFailure { /** no-op */ }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): OpenViewModel
    }
}

@Immutable
data class OpenUiState(
    override val data: OpenData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<OpenData>

@Immutable
data class OpenData(
    val imageUrl: String? = null,
)

sealed interface OpenAction {
    data object DoneClick : OpenAction
}

sealed interface OpenSideEffect {
    data class NavigateToMemory(val capsuleId: Long) : OpenSideEffect
}
