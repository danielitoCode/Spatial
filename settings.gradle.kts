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

rootProject.name = "Spatial"
include(":app")
include(":spatial-core")
include(":spatial-compose")
include(":spatial-math")
include(":spatial-renderer")
include(":spatial-scene")
include(":spatial-camera")
include(":spatial-motion")
include(":spatial-gesture")
include(":spatial-material")
include(":spatial-geometry")
include(":spatial-light")
include(":spatial-units")
include(":spatial-runtime")
