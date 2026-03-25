package com.idiotfrogs.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Routes: NavKey {
    @Serializable
    data object Login : Routes
    @Serializable
    data object SignUp : Routes
    @Serializable
    data object Home : Routes
    @Serializable
    data object Create : Routes
    @Serializable
    data object Profile : Routes
    @Serializable
    data object EditProfile : Routes
    @Serializable
    data object Setting : Routes
    @Serializable
    data class Friend(val id: Int) : Routes
    @Serializable
    data class Detail(val id: Long) : Routes
    @Serializable
    data class Message(val id: Int) : Routes
}
