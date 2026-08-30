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
import kotlin.time.Clock

@HiltViewModel(assistedFactory = WateringDetailViewModel.Factory::class)
class WateringDetailViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getWateringUseCase: GetWateringUseCase,
    private val wateringUseCase: WateringUseCase
): BaseViewModel<WateringDetailState, WateringDetailSideEffect, WateringDetailAction>() {
    override val container: Container<WateringDetailState, WateringDetailSideEffect> = container(
        initialState = WateringDetailState(),
        onCreate = { fetchWatering() },
    )

    override fun onAction(action: WateringDetailAction) {
        when (action) {
            WateringDetailAction.NextWateringRequested -> loadMoreWatering()
            WateringDetailAction.WateringClicked -> watering()
            WateringDetailAction.BackClicked -> navigateToBack()
        }
    }

    private fun fetchWatering() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }
        val result = getWateringUseCase.invoke(
            capsuleId = capsuleId,
            page = 0,
            size = WATERING_DETAIL_LOAD_SIZE,
            sort = SORT_ASC
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
                // 첫 로드 전 임시 데이터 (묻은날 ~ 오늘 ~ 미래까지 totalDays 전체를 빈 셀로 채움)
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
                        data = WateringDetailData(
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
                size = WATERING_DETAIL_LOAD_SIZE,
                sort = SORT_ASC
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
                        data = WateringDetailData(
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

    private fun navigateToBack() {
        intent { postSideEffect(WateringDetailSideEffect.NavigateToBack) }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): WateringDetailViewModel
    }

    companion object {
        const val WATERING_DETAIL_LOAD_SIZE = 50
        const val SORT_ASC = "asc"
    }
}

@Immutable
data class WateringDetailData(
    val waterings: PaginationState<WateringContentResponse> = PaginationState(),
    val stage: Int? = null,
    val wateringCount: Long? = null,
    val totalDays: Long? = null
)

@Immutable
data class WateringDetailState(
    override val data: WateringDetailData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<WateringDetailData>

sealed interface WateringDetailAction {
    data object NextWateringRequested : WateringDetailAction
    data object WateringClicked : WateringDetailAction
    data object BackClicked : WateringDetailAction
}

sealed interface WateringDetailSideEffect {
    data object NavigateToBack : WateringDetailSideEffect
}