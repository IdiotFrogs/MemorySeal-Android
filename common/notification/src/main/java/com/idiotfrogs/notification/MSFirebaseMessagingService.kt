package com.idiotfrogs.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MSFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {}

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        val data = message.data

        NotificationBuilder.show(this, title, body, data)
    }
}
