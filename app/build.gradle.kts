plugins {
    id("convention.android.application")
    id("convention.android.hilt")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:create"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:friend"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":common:resource"))
    implementation(project(":core:di"))

    implementation(libs.androidx.core.splash)

    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    baselineProfile(project(":baselineprofile"))
    implementation(libs.androidx.profileinstaller)
}