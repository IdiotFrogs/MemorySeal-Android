package com.idiotfrogs.detail

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(

) : BaseViewModel<DetailAction>(), ContainerHost<UiState<Unit>, DetailSideEffect> {
    override val container: Container<UiState<Unit>, DetailSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                // TODO 초기 데이터 로딩
                intent { reduce { UiState.Success(Unit) } }
            }
        }
    )

    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.NavigateToFriend -> intent { postSideEffect(DetailSideEffect.NavigateToFriend(action.id)) }
            DetailAction.NavigateToBack -> intent { postSideEffect(DetailSideEffect.NavigateToBack) }
        }
    }
}

sealed interface DetailAction {
    data class NavigateToFriend(val id: Int) : DetailAction
    data object NavigateToBack : DetailAction
}

sealed interface DetailSideEffect {
    data class NavigateToFriend(val id: Int) : DetailSideEffect
    data object NavigateToBack : DetailSideEffect
}