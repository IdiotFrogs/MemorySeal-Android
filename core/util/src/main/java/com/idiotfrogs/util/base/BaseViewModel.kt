package com.idiotfrogs.util.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.util.exception.EmptyTokenException
import com.idiotfrogs.util.exception.LoginCancelledException
import com.idiotfrogs.util.exception.LoginRequiredException
import com.idiotfrogs.util.sideEffect.AppSideEffect
import com.idiotfrogs.util.sideEffect.MSSideEffect
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.orbitmvi.orbit.ContainerHost

abstract class BaseViewModel<STATE: Any, SIDE_EFFECT: Any, ACTION> : ViewModel(), ContainerHost<STATE, SIDE_EFFECT> {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is LoginRequiredException -> MSSideEffect.postSideEffect(AppSideEffect.LoginRequired)
            is LoginCancelledException -> { /** 사용자 취소 toast or alert (필요시) */ }
            is EmptyTokenException -> { /** 빈 토큰 toast or alert (필요시) */ }
            else -> Log.d("TAG", "throwable: ${throwable.message}")
        }
    }
    private val safeScope = viewModelScope + coroutineExceptionHandler

    abstract fun onAction(action: ACTION)

    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        safeScope.launch(block = block)
    }

    protected fun showErrorToast() {
        MSSideEffect.postSideEffect(AppSideEffect.ShowErrorToast)
    }

    /** 화면 로드와 관련된 조회 API 호출 시 사용한다. 실패 할 경우 1회에 한하여 재 조회 한다. */
    protected suspend fun <T> fetchWithRetry(block: suspend () -> Result<T>): Result<T> {
        val result = block()
        if (result.isSuccess) return result

        return block()
    }

    /** [fetchWithRetry]를 async로 실행한다. 재조회까지 끝난 결과를 Deferred로 반환한다. */
    protected fun <T> CoroutineScope.fetchWithAsyncRetry(
        block: suspend () -> Result<T>
    ): Deferred<Result<T>> = async { fetchWithRetry(block) }
}