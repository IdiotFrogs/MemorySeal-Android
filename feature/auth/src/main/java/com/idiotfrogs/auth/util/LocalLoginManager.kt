package com.idiotfrogs.auth.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.idiotfrogs.data.LoginManager

val LocalLoginManager = staticCompositionLocalOf<LoginManager?> {
    null
}