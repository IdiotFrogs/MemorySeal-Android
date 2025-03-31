plugins {
    id("convention.android.library")
    id("convention.android.compose")
}

android {
    namespace = "com.idiotfrogs.resource"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}