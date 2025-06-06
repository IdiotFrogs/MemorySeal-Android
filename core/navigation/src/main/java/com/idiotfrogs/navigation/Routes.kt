package com.idiotfrogs.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Login : Routes
    @Serializable
    data object SignUp : Routes
    @Serializable
    data object Create : Routes
}
