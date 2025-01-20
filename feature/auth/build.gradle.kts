plugins {
    id("convention.android.feature")
}

android {
    namespace = "com.idiotfrogs.auth"
}

dependencies {
    implementation(project(":core:data"))

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}