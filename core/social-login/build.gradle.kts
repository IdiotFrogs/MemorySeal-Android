plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.social_login"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:local"))
    implementation(project(":core:util"))
    implementation(project(":common:extension"))

    // 구글 로그인
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.service)
    implementation(libs.identity.google.id)

    // 애플 로그인
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
