package com.idiotfrogs.memoryseal

import android.app.Application
import com.idiotfrogs.notification.NotificationBuilder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MemorySealApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationBuilder.createChannels(this)
    }
}