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
include(":data")
include(":domain")
include(":common:extension")
include(":common:resource")
include(":common:notification")
include(":core:navigation")
include(":core:network")
include(":core:model")
include(":core:designsystem")
include(":core:di")
include(":core:util")
include(":core:social-login")
include(":core:local")
include(":feature:auth")
include(":feature:home")
include(":feature:detail")
include(":feature:profile")
include(":feature:create")
include(":feature:setting")
include(":feature:friend")
include(":baselineprofile")
