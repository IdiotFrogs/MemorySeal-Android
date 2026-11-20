import com.idiotfrogs.memoryseal.configureComposeAndroid
import com.idiotfrogs.memoryseal.configureKotlinAndroid

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}

android {
    configureKotlinAndroid(this)
    configureComposeAndroid(this)

    namespace = "com.idiotfrogs.memoryseal"

    defaultConfig {
        applicationId = "com.idiotfrogs.memoryseal"
        targetSdk = 36
        versionCode = 10
        versionName = "0.0.10"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
