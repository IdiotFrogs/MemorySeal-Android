package com.idiotfrogs.notification

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenProvider @Inject constructor() {
    suspend fun getToken(): String = FirebaseMessaging.getInstance().token.await()
}
