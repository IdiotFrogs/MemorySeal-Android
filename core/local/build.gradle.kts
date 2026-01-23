plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.local"
}

dependencies {
    implementation(libs.androidx.datastore)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}