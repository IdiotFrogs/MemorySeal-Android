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
}