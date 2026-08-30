package com.idiotfrogs.watering

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetWateringUseCase
import com.idiotfrogs.domain.usecase.timecapsule.WateringUseCase
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.paging.PaginationState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import kotlin.collections.indexOfFirst
import kotlin.time.Clock

@HiltViewModel(assistedFactory = WateringViewModel.Factory::class)
class WateringViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getWateringUseCase: GetWateringUseCase,
    private val wateringUseCase: WateringUseCase
): BaseViewModel<WateringState, WateringSideEffect, WateringAction>() {
    override val container: Container<WateringState, WateringSideEffect> = container(
        initialState = WateringState(),
        onCreate = { fetchWatering() },
    )
    override fun onAction(action: WateringAction) {
        when (action) {
            WateringAction.NextWateringRequested -> loadMoreWatering()
            WateringAction.WateringClicked -> watering()
            WateringAction.BackClicked -> navigateToBack()
            is WateringAction.ShowAllClicked -> navigateToWateringDetail()
        }
    }

    private fun fetchWatering() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }
        val result = getWateringUseCase.invoke(
            capsuleId = capsuleId,
            page = 0,
            size = WATERING_LOAD_SIZE,
            sort = SORT_DESC
        )
        intent {
            if (result.isFailure) {
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            } else {
                val response = result.getOrElse { return@intent }
                // totalElement - 1 = 오늘
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val buriedDay = today.minus(response.waterings.totalElements - 1, DateTimeUnit.DAY)
                // 첫 로드 전 임시 데이터
                val waterings = List(response.totalDays.toInt()) {
                    WateringContentResponse(
                        wateredDate = buriedDay.plus(it, DateTimeUnit.DAY),
                        isWatered = false,
                        userId = null,
                        profileImageUrl = null
                    )
                }.toMutableList()

                response.waterings.content.forEach { remote ->
                    val index = waterings.indexOfFirst { it.wateredDate == remote.wateredDate }
                    // 만약 해당 일자가 빈 데이터로 있다면 채우기
                    if (index != -1) { waterings[index] = remote }
                }

                reduce {
                    state.copy(
                        data = WateringData(
                            stage = response.stage,
                            waterings = PaginationState(
                                items = waterings.toList(),
                                currentPage = 0,
                                totalElements = response.waterings.totalElements,
                                isLast = response.waterings.last,
                                isLoadingMore = false
                            ),
                            wateringCount = response.wateringCount,
                            totalDays = response.totalDays
                        ),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun loadMoreWatering() = safeLaunch {
        intent {
            val currentData = state.data ?: return@intent
            val waterings = currentData.waterings
            if (state.isLoading || !waterings.canLoadMore) return@intent
            val currentPage = waterings.currentPage
            val nextPage = currentPage + 1

            reduce {
                state.copy(
                    data = currentData.copy(
                        waterings = waterings.setLoadingMore(true)
                    )
                )
            }

            getWateringUseCase.invoke(
                capsuleId = capsuleId,
                page = nextPage,
                size = WATERING_LOAD_SIZE,
                sort = SORT_DESC
            ).onSuccess { response ->
                val latestData = state.data ?: return@intent
                val waterings = latestData.waterings
                val isCurrentRequest = waterings.currentPage == currentPage &&
                    waterings.isLoadingMore
                if (!isCurrentRequest) return@onSuccess

                val items = waterings.items.toMutableList()
                response.waterings.content.forEach { remote ->
                    val index = items.indexOfFirst { it.wateredDate == remote.wateredDate }
                    // 만약 해당 일자가 빈 데이터로 있다면 채우기
                    if (index != -1) { items[index] = remote }
                }

                reduce {
                    state.copy(
                        data = WateringData(
                            stage = response.stage,
                            waterings = PaginationState(
                                items = items.toList(),
                                currentPage = response.waterings.number,
                                totalElements = response.waterings.totalElements,
                                isLast = response.waterings.last
                            ),
                            wateringCount = response.wateringCount,
                            totalDays = response.totalDays
                        ),
                    )
                }
            }.onFailure { error ->
                val latestData = state.data ?: return@intent
                val waterings = latestData.waterings
                val isCurrentRequest = waterings.currentPage == currentPage &&
                        waterings.isLoadingMore
                if (!isCurrentRequest) return@onFailure


                reduce {
                    state.copy(
                        data = currentData.copy(
                            waterings = waterings.setLoadingMore(false)
                        ),
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun watering() {
        safeLaunch {
            wateringUseCase.invoke(capsuleId)
                .onSuccess { fetchWatering() } // 실제 0페이지 데이터를 다시 불러와 업데이트 처리
        }
    }

    private fun navigateToWateringDetail() {
        intent { postSideEffect(WateringSideEffect.NavigateToWateringDetail(capsuleId)) }
    }

    private fun navigateToBack() {
        intent { postSideEffect(WateringSideEffect.NavigateToBack) }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): WateringViewModel
    }

    companion object {
        const val WATERING_LOAD_SIZE = 20
        const val SORT_DESC = "desc"
    }
}

@Immutable
data class WateringData(
    val waterings: PaginationState<WateringContentResponse> = PaginationState(),
    val stage: Int? = null,
    val wateringCount: Long? = null,
    val totalDays: Long? = null
)

@Immutable
data class WateringState(
    override val data: WateringData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<WateringData>

sealed interface WateringAction {
    data object NextWateringRequested : WateringAction
    data object WateringClicked : WateringAction
    data object BackClicked : WateringAction
    data object ShowAllClicked : WateringAction
}

sealed interface WateringSideEffect {
    data object NavigateToBack : WateringSideEffect
    data class NavigateToWateringDetail(val id: Long) : WateringSideEffect
}