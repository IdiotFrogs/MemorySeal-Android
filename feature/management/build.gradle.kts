plugins {
    id("convention.android.feature")
}

android {
    namespace = "com.idiotfrogs.management"
}

dependencies {
    implementation(project(":common:extension"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}