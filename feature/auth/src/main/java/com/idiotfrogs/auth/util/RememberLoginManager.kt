package com.idiotfrogs.auth.util

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.skogkatt.social_login.LoginManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@Composable
internal fun rememberLoginManager(): LoginManager? {
    val activity = LocalActivity.current

    activity?.let {
        return remember {
            val entryPoint = EntryPointAccessors.fromActivity(
                activity = activity,
                entryPoint = LoginManagerEntryPoint::class.java
            )
            entryPoint.loginManager()
        }
    } ?: return null
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface LoginManagerEntryPoint {
    fun loginManager(): LoginManager
}