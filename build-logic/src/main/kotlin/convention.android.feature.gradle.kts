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
    "implementation"(project(":data"))
    "implementation"(project(":domain"))
    "implementation"(project(":common:resource"))
    "implementation"(project(":common:extension"))
    "implementation"(project(":core:designsystem"))
    "implementation"(project(":core:util"))
    "implementation"(project(":core:model"))
    "implementation"(project(":core:model"))
    "implementation"(project(":core:navigation"))
}
