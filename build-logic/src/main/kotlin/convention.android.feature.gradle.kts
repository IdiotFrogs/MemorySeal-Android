import com.idiotfrogs.memoryseal.configureComposeAndroid

plugins {
    id("convention.android.library")
    id("convention.android.hilt")
    kotlin("plugin.compose")
}

android {
    configureComposeAndroid(this)
}

dependencies {
    "implementation"(project(":common:resource"))
    "implementation"(project(":core:designsystem"))
}
