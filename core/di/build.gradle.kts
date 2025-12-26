plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.di"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core:network"))

    implementation(libs.retrofit)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}