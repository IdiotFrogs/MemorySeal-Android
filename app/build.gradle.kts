import com.idiotfrogs.memoryseal.localStringBuildConfigField

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

    flavorDimensions += "server"
    productFlavors {
        create("dev") {
            dimension = "server"
            applicationIdSuffix = ".dev"
        }
        create("prod") {
            dimension = "server"
        }
    }
}

androidComponents {
    onVariants { variant ->
        val server = variant.productFlavors.single { it.first == "server" }.second
        val baseUrlProperty = if (server == "dev") "DEV_BASE_URL" else "BASE_URL"
        checkNotNull(variant.buildConfigFields)
            .put("BASE_URL", localStringBuildConfigField(baseUrlProperty))
    }
}

dependencies {
    implementation(project(":feature:splash"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:create"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:friend"))
    implementation(project(":feature:management"))
    implementation(project(":feature:message"))
    implementation(project(":feature:preview"))
    implementation(project(":feature:memory"))
    implementation(project(":feature:watering"))
    implementation(project(":feature:open"))
    implementation(project(":feature:tutorial"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:util"))
    implementation(project(":core:di"))
    implementation(project(":core:network"))
    implementation(project(":core:analytics"))
    implementation(project(":core:app-link"))
    implementation(project(":common:resource"))
    implementation(project(":common:notification"))

    implementation(libs.androidx.core.splash)

    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    baselineProfile(project(":baselineprofile"))
    implementation(libs.androidx.profileinstaller)
}
