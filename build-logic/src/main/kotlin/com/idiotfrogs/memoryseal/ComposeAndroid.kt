package com.idiotfrogs.memoryseal

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

internal fun Project.configureComposeAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    with(plugins) {
        apply("com.github.skydoves.compose.stability.analyzer")
    }

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))

            add("implementation", libs.findLibrary("androidx-compose-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-activity-compose").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
            add("implementation", libs.findLibrary("androidx-navigation").get())
            add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
            add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
            add("implementation", libs.findLibrary("hilt-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("androidx-navigation3-lifecycle").get())

            add("implementation", libs.findLibrary("orbit-compose").get())
            add("implementation", libs.findLibrary("orbit-viewmodel").get())

            add("implementation", libs.findLibrary("landscapist-glide").get())

            add("implementation", libs.findLibrary("haze").get())
        }

        extensions.configure<ComposeCompilerGradlePluginExtension> {
//            featureFlags.add(ComposeFeatureFlag.StrongSkipping) // StrongSkipping, IntrinsicRemember 은 kotlin 2.0.20 부터 기본적으로 활성화 상태
//            featureFlags.add(ComposeFeatureFlag.IntrinsicRemember)
            featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
        }
    }
}