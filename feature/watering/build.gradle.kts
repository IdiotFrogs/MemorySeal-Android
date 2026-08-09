plugins {
    id("convention.android.feature")
}

android {
    namespace = "com.idiotfrogs.watering"
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}