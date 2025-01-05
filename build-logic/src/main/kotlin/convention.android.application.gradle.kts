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
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}