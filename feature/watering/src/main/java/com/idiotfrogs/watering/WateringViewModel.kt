package com.idiotfrogs.watering

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.idiotfrogs.domain.usecase.timecapsule.GetWateringUseCase
import com.idiotfrogs.domain.usecase.timecapsule.WateringUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel(assistedFactory = WateringViewModel.Factory::class)
class WateringViewModel @Inject constructor(
    @Assisted private val capsuleId: Long,
    private val getWateringUseCase: GetWateringUseCase,
    private val wateringUseCase: WateringUseCase
): BaseViewModel<WateringState, WateringSideEffect, WateringAction>() {
    override val container = container<WateringState, WateringSideEffect>(WateringState())

    // 유저 물 주기 등 강제 리프레시가 필요한 경우 트리거로 사용
    private val refreshEvent = MutableSharedFlow<Unit>()

    // 핸들링 용이성과 관리 관점에서 볼 때 통합하지 말고 별도 상태로 관리
    @OptIn(ExperimentalCoroutinesApi::class)
    val watering = refreshEvent
        .onStart { emit(Unit) } // 최초 1회 조회 트리거
        .flatMapLatest { getWateringUseCase.invoke(capsuleId) }
        .cachedIn(viewModelScope) // pagingData 캐싱

    override fun onAction(action: WateringAction) {
        when (action) {
            WateringAction.WateringClicked -> watering()
            WateringAction.BackClicked -> navigateToBack()
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
        intent { postSideEffect(WateringSideEffect.NavigateToBack) }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): WateringViewModel
    }
}

data class WateringState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
): BaseUiState

sealed interface WateringAction {
    data object WateringClicked : WateringAction
    data object BackClicked : WateringAction
}

sealed interface WateringSideEffect {
    data object NavigateToBack : WateringSideEffect
}

