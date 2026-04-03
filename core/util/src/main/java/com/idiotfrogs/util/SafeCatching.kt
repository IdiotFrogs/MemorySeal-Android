package com.idiotfrogs.util

import com.idiotfrogs.util.exception.LoginRequiredException
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> safeCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: LoginRequiredException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}