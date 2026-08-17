package com.idiotfrogs.watering

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.idiotfrogs.domain.usecase.timecapsule.GetWateringUseCase
import com.idiotfrogs.domain.usecase.timecapsule.WateringUseCase
import com.idiotfrogs.model.timecapsule.WateringMeta
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = WateringDetailViewModel.Factory::class)
class WateringDetailViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getWateringUseCase: GetWateringUseCase,
    private val wateringUseCase: WateringUseCase
): BaseViewModel<WateringDetailState, WateringDetailSideEffect, WateringDetailAction>() {
    override val container = container<WateringDetailState, WateringDetailSideEffect>(WateringDetailState())

    // 유저 물 주기 등 강제 리프레시가 필요한 경우 트리거로 사용
    private val refreshEvent = MutableSharedFlow<Unit>()

    // 핸들링 용이성과 관리 관점에서 볼 때 통합하지 말고 별도 상태로 관리
    @OptIn(ExperimentalCoroutinesApi::class)
    val watering = refreshEvent
        .onStart { emit(Unit) } // 최초 1회 조회 트리거
        .flatMapLatest {
            getWateringUseCase.invoke(capsuleId, "asc") {
                intent {
                    reduce { state.copy(data = it) }
                }
            }
        }
        .cachedIn(viewModelScope) // pagingData 캐싱

    override fun onAction(action: WateringDetailAction) {
        when (action) {
            WateringDetailAction.WateringClicked -> watering()
            WateringDetailAction.BackClicked -> navigateToBack()
        }
    }

    private fun watering() {
        safeLaunch {
            wateringUseCase.invoke(capsuleId)
                .onSuccess { refreshEvent.emit(Unit) }
                .onFailure { /** TODO: 에러 핸들링 */ }
        }
    }

    private fun navigateToBack() {
        intent { postSideEffect(WateringDetailSideEffect.NavigateToBack) }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): WateringDetailViewModel
    }
}

data class WateringDetailState(
    override val isLoading: Boolean = false,
    override val data: WateringMeta? = null,
    override val errorMessage: String? = null,
): DataUiState<WateringMeta>

sealed interface WateringDetailAction {
    data object WateringClicked : WateringDetailAction
    data object BackClicked : WateringDetailAction
}

sealed interface WateringDetailSideEffect {
    data object NavigateToBack : WateringDetailSideEffect
}

