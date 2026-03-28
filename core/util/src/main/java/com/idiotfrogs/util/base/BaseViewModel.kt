package com.idiotfrogs.util.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.util.exception.LoginRequiredException
import com.idiotfrogs.util.sideEffect.AppSideEffect
import com.idiotfrogs.util.sideEffect.MSSideEffect
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel<ACTION> : ViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is LoginRequiredException) {
            MSSideEffect.postSideEffect(AppSideEffect.LoginRequired)
        } else {
            Log.d("TAG", "throwable: ${throwable.message}")
        }
    }
    private val safeScope = viewModelScope + coroutineExceptionHandler

    abstract fun onAction(action: ACTION)

    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        safeScope.launch(block = block)
    }
}