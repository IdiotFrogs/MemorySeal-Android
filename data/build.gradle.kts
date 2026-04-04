plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:local"))
    implementation(project(":common:extension"))

    implementation(libs.okhttp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}