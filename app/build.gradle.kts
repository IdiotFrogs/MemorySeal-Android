import java.util.Properties

plugins {
    id("convention.android.application")
    id("convention.android.hilt")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.google.services)
}

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "APPSFLYER_DEV_KEY", getLocalProperty("APPSFLYER_DEV_KEY"))
        buildConfigField("String", "APPSFLYER_ONELINK_TEMPLATE_ID", getLocalProperty("APPSFLYER_ONELINK_TEMPLATE_ID"))
    }
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
    implementation(project(":feature:message"))
    implementation(project(":feature:preview"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:util"))
    implementation(project(":core:di"))
    implementation(project(":common:resource"))
    implementation(project(":common:notification"))

    implementation(libs.androidx.core.splash)
    implementation(libs.appsflyer)

    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    baselineProfile(project(":baselineprofile"))
    implementation(libs.androidx.profileinstaller)
}

fun getLocalProperty(name: String): String {
    val propertiesFile = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(propertiesFile.inputStream())
    return properties.getProperty(name)
}
