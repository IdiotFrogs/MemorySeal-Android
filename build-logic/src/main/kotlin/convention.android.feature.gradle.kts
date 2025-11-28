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
    "implementation"(project(":core:data"))
    "implementation"(project(":core:domain"))
    "implementation"(project(":core:util"))
    implementation(project(":core:navigation"))
}
