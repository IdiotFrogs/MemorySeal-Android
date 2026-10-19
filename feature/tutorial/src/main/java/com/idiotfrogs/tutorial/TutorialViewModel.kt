package com.idiotfrogs.tutorial

import com.idiotfrogs.domain.usecase.local.SetTutorialCompletedUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val setTutorialCompletedUseCase: SetTutorialCompletedUseCase,
) : BaseViewModel<TutorialUiState, TutorialSideEffect, TutorialAction>() {

    override val container: Container<TutorialUiState, TutorialSideEffect> = container(TutorialUiState())

    override fun onAction(action: TutorialAction) {
        when (action) {
            TutorialAction.Completed -> complete()
        }
    }

    /** 건너뛰기 또는 마지막 페이지 완료. 디바이스 단위로 시청 완료를 저장한 뒤 홈으로 이동한다. */
    private fun complete() {
        safeLaunch {
            setTutorialCompletedUseCase()
            intent { postSideEffect(TutorialSideEffect.NavigateToHome) }
        }
    }
}

data class TutorialUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface TutorialAction {
    data object Completed : TutorialAction
}

sealed interface TutorialSideEffect {
    data object NavigateToHome : TutorialSideEffect
}
