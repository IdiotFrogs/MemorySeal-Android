plugins {
    id("convention.android.library")
}

android {
    namespace = "com.idiotfrogs.notification"
}

dependencies {
    implementation(project(":common:resource"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
