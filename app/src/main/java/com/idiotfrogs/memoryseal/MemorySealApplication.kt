package com.idiotfrogs.memoryseal

import android.app.Application
import com.appsflyer.AppsFlyerLib
import com.appsflyer.share.deeplink.DeepLinkResult
import com.idiotfrogs.notification.NotificationBuilder
import com.idiotfrogs.util.sideEffect.AppSideEffect
import com.idiotfrogs.util.sideEffect.MSSideEffect
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MemorySealApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppsFlyer()
        NotificationBuilder.createChannels(this)
    }

    private fun initAppsFlyer() {
        val devKey = BuildConfig.APPSFLYER_DEV_KEY
        val oneLinkTemplateId = BuildConfig.APPSFLYER_ONELINK_TEMPLATE_ID

        if (devKey.isBlank() || oneLinkTemplateId.isBlank()) return

        val appsFlyer = AppsFlyerLib.getInstance()
        if (BuildConfig.DEBUG) appsFlyer.setDebugLog(true)

        appsFlyer.setAppInviteOneLink(oneLinkTemplateId)
        appsFlyer.subscribeForDeepLink { result ->
            if (result.status != DeepLinkResult.Status.FOUND) return@subscribeForDeepLink

            result.deepLink
                ?.deepLinkValue
                ?.toLongOrNull()
                ?.let { capsuleId ->
                    MSSideEffect.postSideEffect(
                        AppSideEffect.TimeCapsuleInviteLinkOpened(capsuleId)
                    )
                }
        }
        appsFlyer.init(devKey, null, this)
        appsFlyer.registerSessionReadyListener {
            appsFlyer.start()
        }
    }
}
