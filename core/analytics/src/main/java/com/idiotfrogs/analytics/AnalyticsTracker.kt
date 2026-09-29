package com.idiotfrogs.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun visitedScreen(screenName: String) {
        analytics.logEvent("visited_screen", Bundle().apply {
            putString("screen_name", screenName)
        })
    }

    fun ticketCreated() {
        analytics.logEvent("create_ticket", null)
    }

    fun messageAdded(type: String) {
        analytics.logEvent("add_message", Bundle().apply {
            putString("type", type)
        })
    }

    fun ticketBuried(createdDays: Long, buriedDays: Long) {
        analytics.logEvent("buried_ticket", Bundle().apply {
            putLong("created_day", createdDays)
            putLong("buried_day", buriedDays)
        })
    }

    fun ticketWatered(stage: Int) {
        analytics.logEvent("watering_ticket", Bundle().apply {
            putLong("stage", stage.toLong())
        })
    }

    fun ticketOpened(openedDays: Long) {
        analytics.logEvent("open_ticket", Bundle().apply {
            putLong("opened_day", openedDays)
        })
    }

    fun openedTicketVisited() {
        analytics.logEvent("visit_opened_ticket", null)
    }
}
