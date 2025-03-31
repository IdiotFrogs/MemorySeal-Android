plugins {
    id("convention.android.feature")
}

android {
    namespace = "com.idiotfrogs.auth"
}

dependencies {
    implementation(project(":common:resource"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}