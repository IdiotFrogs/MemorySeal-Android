plugins {
    id("convention.android.library")
}

android {
    namespace = "com.idiotfrogs.util"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.orbit.viewmodel)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}