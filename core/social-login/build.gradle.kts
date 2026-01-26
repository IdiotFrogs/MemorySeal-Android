import java.util.Properties

plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.social_login"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "WEB_CLIENT_ID", getLocalProperty("WEB_CLIENT_ID"))
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:local"))
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

fun getLocalProperty(name: String): String {
    val propertiesFile = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(propertiesFile.inputStream())
    return properties.getProperty(name)
}