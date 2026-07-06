package com.idiotfrogs.network.util

import com.idiotfrogs.util.error.DomainError
import com.idiotfrogs.util.exception.LoginRequiredException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

fun Throwable.toDomainError(): DomainError {
    val exception = this as? HttpException ?: throw this
    return when (exception.code()) {
        400 -> DomainError.BadRequest(exception.message.orEmpty())
        401 -> DomainError.Unauthorized(exception.message.orEmpty())
        403 -> DomainError.Forbidden(exception.message.orEmpty())
        404 -> DomainError.NotFound(exception.message.orEmpty())
        409 -> DomainError.Conflict(exception.message.orEmpty())
        500 -> DomainError.Server(exception.message.orEmpty())
        else -> throw exception
    }
}

inline fun <T> mapToDomainError(block: () -> T): T = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: LoginRequiredException) {
    throw e
} catch (e: Exception) {
    throw e.toDomainError()
}