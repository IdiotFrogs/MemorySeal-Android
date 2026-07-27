package com.idiotfrogs.util.error

sealed class DomainError(message: String) : Exception(message) {
    data class BadRequest(override val message: String): DomainError(message)
    data class Unauthorized(override val message: String): DomainError(message)
    data class Forbidden(override val message: String): DomainError(message)
    data class NotFound(override val message: String): DomainError(message)
    data class Conflict(override val message: String): DomainError(message)
    data class Server(override val message: String): DomainError(message)
}