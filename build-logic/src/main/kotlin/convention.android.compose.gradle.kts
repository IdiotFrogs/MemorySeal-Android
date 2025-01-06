import com.idiotfrogs.memoryseal.configureComposeAndroid

plugins {
    id("convention.android.library")
    kotlin("plugin.compose")
}

android {
    configureComposeAndroid(this)
}