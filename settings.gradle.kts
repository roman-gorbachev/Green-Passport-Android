pluginManagement {
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GreenPassport"
include(":app")

include(":core:model")
include(":core:common")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")
include(":core:database")
include(":core:storage")
include(":core:auth")
include(":core:messaging")
include(":core:datastore")

listOf(
    "auth",
    "profile",
    "tasks",
    "shop",
    "calendar",
    "map",
    "community",
    "ecotips",
    "feedback",
    "games",
).forEach { feature ->
    include(":feature:$feature:domain")
    include(":feature:$feature:presentation")
}
