package com.idiotfrogs.auth.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.idiotfrogs.navigation.Routes

fun NavGraphBuilder.loginScreen() {
    composable<Routes.Login> {
        LoginScreen()
    }
}