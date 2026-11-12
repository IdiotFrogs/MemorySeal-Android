import com.android.build.api.variant.BuildConfigField
import java.io.Serializable
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

val localProperties = providers.fileContents(
    rootProject.layout.projectDirectory.file("local.properties"),
).asText.orElse("").map { contents ->
    Properties().apply { load(contents.reader()) }
}

androidComponents {
    onVariants { variant ->
        val server = variant.productFlavors.single { it.first == "server" }.second
        val isDev = server == "dev"
        val propertyNames = mapOf(
            "BASE_URL" to if (isDev) "DEV_BASE_URL" else "BASE_URL",
            "WEB_CLIENT_ID" to if (isDev) "DEV_WEB_CLIENT_ID" else "WEB_CLIENT_ID",
        )
        val buildConfigFields = checkNotNull(variant.buildConfigFields)

        for ((fieldName, propertyName) in propertyNames) {
            val field = localProperties.map { properties ->
                val value = checkNotNull(properties.getProperty(propertyName)) {
                    "Add $propertyName to local.properties"
                }.trim().removeSurrounding("\"")
                val escapedValue = value.replace("\\", "\\\\").replace("\"", "\\\"")
                BuildConfigField<Serializable>("String", "\"$escapedValue\"", null)
            }
            buildConfigFields.put(fieldName, field)
        }
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
    implementation(project(":core:social-login"))
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
