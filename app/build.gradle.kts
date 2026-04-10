plugins {
    id("convention.android.application")
    id("convention.android.hilt")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(project(":feature:splash"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:create"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:friend"))
    implementation(project(":feature:management"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:util"))
    implementation(project(":core:di"))
    implementation(project(":common:resource"))

    implementation(libs.androidx.core.splash)

    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    baselineProfile(project(":baselineprofile"))
    implementation(libs.androidx.profileinstaller)
}