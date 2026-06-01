plugins {
    id("convention.android.feature")
}

android {
    namespace = "com.idiotfrogs.splash"
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)

    implementation(libs.accompanist.permissions)
}