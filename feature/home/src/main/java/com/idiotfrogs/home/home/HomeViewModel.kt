package com.idiotfrogs.home.home

import android.util.Log
import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.auth.PutFcmTokenUseCase
import com.idiotfrogs.domain.usecase.home.GetSeasonBannerUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.RequestCollaboratorUseCase
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
import com.idiotfrogs.home.home.HomeSideEffect.*
import com.idiotfrogs.model.home.SeasonBannerResponse
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleContent
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.navigation.HomeDetailType
import com.idiotfrogs.notification.FcmTokenProvider
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.paging.PaginationState
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val requestCollaboratorUseCase: RequestCollaboratorUseCase,
    private val fcmTokenProvider: FcmTokenProvider,
    private val putFcmTokenUseCase: PutFcmTokenUseCase,
    private val getSeasonBannerUseCase: GetSeasonBannerUseCase
): BaseViewModel<HomeUiState, HomeSideEffect, HomeAction>() {

    override val container: Container<HomeUiState, HomeSideEffect> = container(
        initialState = HomeUiState(),
        onCreate = {
            fetchHome()
            fetchOpened()
            syncFcmToken()
            RefreshSideEffect.events.collect {
                if (it is RefreshEvent.Home) {
                    fetchHome()
                    fetchOpened()
                }
            }
        }
    )

    private fun syncFcmToken() {
        safeLaunch {
            val fcmToken = fcmTokenProvider.getToken()
            putFcmTokenUseCase(fcmToken).onFailure {
                // TODO 로그 남기기 (Firebase)
            }
        }
    }

    private fun fetchHome() {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            val userDeferred = async { getMyProfileUseCase() }
            val beforeBuriedDeferred = async {
                getMyTimeCapsuleUseCase(
                    status = TimeCapsuleStatus.BEFOREBURIED, page = 0, size = 6
                )
            }
            val buriedDeferred = async {
                getMyTimeCapsuleUseCase(
                    status = TimeCapsuleStatus.BURIED, page = 0, size = 6
                )
            }
            val seasonBannerDeferred = async { getSeasonBannerUseCase.invoke() }

            val userResult = userDeferred.await()
            val beforeBuriedResult = beforeBuriedDeferred.await()
            val buriedResult = buriedDeferred.await()
            val seasonBannerResponse = seasonBannerDeferred.await()

            val results = listOf(userResult, beforeBuriedResult, buriedResult, seasonBannerResponse)

            intent {
                if (results.any { it.isFailure }) {
                    val errorMessage = results.first { it.isFailure }.exceptionOrNull()?.message

                    Log.d("TTT", errorMessage.toString())
                    reduce { state.copy(isLoading = false, errorMessage = errorMessage) }
                } else {
                    reduce {
                        state.copy(
                            data = HomeData(
                                user = userResult.getOrNull(),
                                beforeBuried = beforeBuriedResult.getOrNull()?.content ?: emptyList(),
                                buried = buriedResult.getOrNull()?.content ?: emptyList(),
                                seasonBanner = seasonBannerResponse.getOrNull()
                            ),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    private fun fetchOpened() = intent {
        intent { reduce { state.copy(isLoading = true) } }

        getMyTimeCapsuleUseCase(
            status = TimeCapsuleStatus.OPENED,
            page = 0,
            size = 10,
        ).onSuccess {
            val latestData = state.data ?: return@onSuccess
            intent {
                reduce {
                    state.copy(
                        data = latestData.copy(
                            opened = PaginationState<MyTimeCapsuleContent>().addPage(it)
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

    private fun loadNextOpened() = intent {
        val currentData = state.data ?: return@intent
        val opened = currentData.opened

        if (state.isLoading || !opened.canLoadMore) return@intent

        val currentPage = opened.currentPage
        val nextPage = currentPage + 1

        reduce {
            state.copy(
                data = currentData.copy(
                    opened = opened.setLoadingMore(true),
                ),
            )
        }

        getMyTimeCapsuleUseCase(
            status = TimeCapsuleStatus.OPENED,
            page = nextPage,
            size = 10,
        ).onSuccess { response ->
            val latestData = state.data ?: return@onSuccess
            val latestOpened = latestData.opened
            val isCurrentRequest = latestOpened.currentPage == currentPage &&
                    latestOpened.isLoadingMore

            if (!isCurrentRequest) return@onSuccess

            reduce {
                state.copy(
                    data = latestData.copy(
                        opened = latestOpened.addPage(response),
                    ),
                    errorMessage = null,
                )
            }
        }.onFailure { error ->
            val latestData = state.data ?: return@onFailure
            val latestOpened = latestData.opened
            val isCurrentRequest = latestOpened.currentPage == currentPage &&
                    latestOpened.isLoadingMore

            if (!isCurrentRequest) return@onFailure

            reduce {
                state.copy(
                    data = latestData.copy(
                        opened = latestOpened.setLoadingMore(false),
                    ),
                    errorMessage = error.message,
                )
            }
        }
    }

    private fun refreshOpened() {
        intent {
            val homeData = state.data
            val seasonBanner = getSeasonBannerUseCase.invoke()
            reduce {
                state.copy(
                    data = homeData?.copy(
                        opened = PaginationState(),
                        seasonBanner = seasonBanner.getOrNull()
                    )
                )
            }
        }
        loadNextOpened()
    }

    private fun requestCollaborator(body: PendingCollaboratorsRequest) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        requestCollaboratorUseCase(body).onSuccess {
            intent {
                reduce { state.copy(isLoading = false, errorMessage = null) }
                fetchHome()
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            // TODO 추 후 에러 핸들링 맞추기 (공동 작업자 이미 신청한 사용자라면 409)
        }
    }

    override fun onAction(action: HomeAction) {
        intent {
            when (action) {
                HomeAction.CreateClicked -> postSideEffect(HomeSideEffect.NavigateToCreate)
                HomeAction.ProfileClicked -> postSideEffect(HomeSideEffect.NavigateToProfile)
                is HomeAction.TimeCapsuleClicked -> postSideEffect(NavigateToDetail(action.id))
                is HomeAction.JoinCodeSubmitted -> requestCollaborator(PendingCollaboratorsRequest(action.code))
                HomeAction.RefreshHome -> fetchHome()
                is HomeAction.HomeDetailClicked -> postSideEffect(NavigateToHomeDetail(action.homeDetailType))
                HomeAction.NextOpenedPageRequested -> loadNextOpened()
                HomeAction.RefreshOpened -> refreshOpened()
            }
        }
    }
}

@Immutable
data class HomeUiState(
    override val data: HomeData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<HomeData>

@Immutable
data class HomeData(
    val user: ProfileResponse? = null,
    val beforeBuried: List<MyTimeCapsuleContent> = emptyList(),
    val buried: List<MyTimeCapsuleContent> = emptyList(),
    val opened: PaginationState<MyTimeCapsuleContent> = PaginationState(),
    val seasonBanner: SeasonBannerResponse? = null,
)

internal fun PaginationState<MyTimeCapsuleContent>.addPage(
    response: MyTimeCapsuleResponse,
): PaginationState<MyTimeCapsuleContent> = addPage(
    newItems = response.content,
    page = response.number,
    totalElements = response.totalElements,
    isLast = response.last,
)

sealed interface HomeAction {
    data object CreateClicked : HomeAction
    data object ProfileClicked : HomeAction
    data class TimeCapsuleClicked(val id: Long) : HomeAction
    data class JoinCodeSubmitted(val code: String) : HomeAction
    data object RefreshHome : HomeAction
    data object RefreshOpened : HomeAction
    data object NextOpenedPageRequested : HomeAction
    data class HomeDetailClicked(val homeDetailType: HomeDetailType) : HomeAction
}

sealed interface HomeSideEffect {
    data object NavigateToCreate : HomeSideEffect
    data object NavigateToProfile : HomeSideEffect
    data class NavigateToDetail(val id: Long) : HomeSideEffect
    data class NavigateToHomeDetail(val homeDetailType: HomeDetailType) : HomeSideEffect
}
