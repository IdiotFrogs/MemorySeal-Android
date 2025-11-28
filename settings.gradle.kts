pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MemorySeal"

include(":app")
include(":common:extension")
include(":common:resource")
include(":common:notification")
include(":core:navigation")
include(":core:network")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:designsystem")
include(":core:datastore")
include(":core:di")
include(":core:util")
include(":feature:auth")
include(":feature:home")
include(":feature:detail")
include(":feature:profile")
include(":feature:create")
include(":feature:setting")
include(":feature:friend")
include(":widget")
include(":baselineprofile")
