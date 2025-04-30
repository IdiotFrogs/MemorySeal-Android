package com.idiotfrogs.auth.util

import com.idiotfrogs.data.LoginManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface LoginManagerEntryPoint {
    fun getLoginManager(): LoginManager
}
