import java.util.Properties

plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.di"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", getLocalProperty("BASE_URL"))
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core:network"))

    implementation(libs.androidx.datastore)

    implementation(libs.retrofit)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}

fun getLocalProperty(name: String): String {
    val propertiesFile = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(propertiesFile.inputStream())
    return properties.getProperty(name)
}