package com.idiotfrogs.memoryseal

import com.android.build.api.variant.BuildConfigField
import java.io.Serializable
import java.util.Properties
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.localStringBuildConfigField(propertyName: String): Provider<BuildConfigField<Serializable>> =
    providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
        .asText.orElse("").map { contents ->
            val properties = Properties().apply { load(contents.reader()) }
            val value = checkNotNull(properties.getProperty(propertyName)) {
                "Add $propertyName to local.properties"
            }.trim().removeSurrounding("\"")
            val escapedValue = value.replace("\\", "\\\\").replace("\"", "\\\"")
            BuildConfigField<Serializable>("String", "\"$escapedValue\"", null)
        }
