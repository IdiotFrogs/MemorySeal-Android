import java.util.Properties

plugins {
    id("convention.android.library")
    id("convention.android.hilt")
}

android {
    namespace = "com.idiotfrogs.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", getLocalProperty("BASE_URL"))
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:util"))
    implementation(project(":core:local"))

    implementation(libs.retrofit)
    implementation(libs.okhttp)

    implementation(libs.kotlinx.serialization.json)

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