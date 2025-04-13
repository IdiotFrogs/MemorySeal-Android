plugins {
    id("convention.android.compose")
}

android {
    namespace = "com.idiotfrogs.designsystem"
}

dependencies {
    implementation(project(":common:resource"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}