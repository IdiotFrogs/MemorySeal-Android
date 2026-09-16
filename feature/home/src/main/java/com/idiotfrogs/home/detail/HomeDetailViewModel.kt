package com.idiotfrogs.home.detail

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.home.detail.HomeDetailSideEffect.*
import com.idiotfrogs.home.home.addPage
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleContent
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.navigation.HomeDetailType
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.paging.PaginationState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = HomeDetailViewModel.Factory::class)
class HomeDetailViewModel @AssistedInject constructor(
    @Assisted private val homeDetailType: HomeDetailType,
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase
) : BaseViewModel<HomeDetailUiState, HomeDetailSideEffect, HomeDetailAction>() {
    override val container: Container<HomeDetailUiState, HomeDetailSideEffect> = container(
        initialState = HomeDetailUiState(),
        onCreate = { fetchItem() }
    )

    override fun onAction(action: HomeDetailAction) {
        when (action) {
            is HomeDetailAction.CapsuleClicked -> intent { postSideEffect(NavigateToDetail(action.id)) }
            HomeDetailAction.NextItemRequested -> loadMoreItem()
            HomeDetailAction.BackClicked -> intent { postSideEffect(NavigateToBack) }
        }
    }

    private fun fetchItem() = intent {
        intent { reduce { state.copy(isLoading = true) } }

        getMyTimeCapsuleUseCase(
            status = when (homeDetailType) {
                HomeDetailType.BURIED -> TimeCapsuleStatus.BURIED
                HomeDetailType.BEFORE_BURIED -> TimeCapsuleStatus.BEFOREBURIED
            },
            page = 0,
            size = 10,
        ).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = HomeDetailData(
                            detailItems = PaginationState<MyTimeCapsuleContent>().addPage(it)
                        ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure {
            intent {
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = it.message
                    )
                }
            }
        }
    }

    private fun loadMoreItem() = intent {
        val currentData = state.data ?: return@intent
        val items = currentData.detailItems

        if (state.isLoading || !items.canLoadMore) return@intent

        val currentPage = items.currentPage
        val nextPage = currentPage + 1

        reduce {
            state.copy(
                data = currentData.copy(
                    detailItems = items.setLoadingMore(true),
                ),
            )
        }

        getMyTimeCapsuleUseCase(
            status = when (homeDetailType) {
                HomeDetailType.BURIED -> TimeCapsuleStatus.BURIED
                HomeDetailType.BEFORE_BURIED -> TimeCapsuleStatus.BEFOREBURIED
            },
            page = nextPage,
            size = 10,
        ).onSuccess { response ->
            val latestData = state.data ?: return@onSuccess
            val latestOpened = latestData.detailItems
            val isCurrentRequest = latestOpened.currentPage == currentPage &&
                    latestOpened.isLoadingMore

            if (!isCurrentRequest) return@onSuccess

            reduce {
                state.copy(
                    data = latestData.copy(
                        detailItems = latestOpened.addPage(response),
                    ),
                    errorMessage = null,
                )
            }
        }.onFailure { error ->
            val latestData = state.data ?: return@onFailure
            val latestOpened = latestData.detailItems
            val isCurrentRequest = latestOpened.currentPage == currentPage &&
                    latestOpened.isLoadingMore

            if (!isCurrentRequest) return@onFailure

            reduce {
                state.copy(
                    data = latestData.copy(
                        detailItems = latestOpened.setLoadingMore(false),
                    ),
                    errorMessage = error.message,
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(homeDetailType: HomeDetailType): HomeDetailViewModel
    }
}

@Immutable
data class HomeDetailUiState(
    override val data: HomeDetailData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<HomeDetailData>

@Immutable
data class HomeDetailData(
    val detailItems: PaginationState<MyTimeCapsuleContent> = PaginationState(),
)

sealed interface HomeDetailAction {
    data class CapsuleClicked(val id: Long) : HomeDetailAction
    data object BackClicked : HomeDetailAction
    data object NextItemRequested : HomeDetailAction
}

sealed interface HomeDetailSideEffect {
    data object NavigateToBack : HomeDetailSideEffect
    data class NavigateToDetail(val id: Long) : HomeDetailSideEffect
}
