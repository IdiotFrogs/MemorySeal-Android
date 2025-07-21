package com.idiotfrogs.auth.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.idiotfrogs.data.LoginManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@Composable
internal fun rememberLoginManager(): LoginManager {
    val activity = LocalContext.current as ComponentActivity
    return remember {
        val entryPoint = EntryPointAccessors.fromActivity(
            activity = activity,
            entryPoint = LoginManagerEntryPoint::class.java
        )
        entryPoint.loginManager()
    }
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface LoginManagerEntryPoint {
    fun loginManager(): LoginManager
}