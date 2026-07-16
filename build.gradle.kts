// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

tasks.register("publishAllModules") {

    description = "A module publishing task"
    dependsOn(
        ":spatial-math:publishToMavenCentral",
        ":spatial-units:publishToMavenCentral",
        ":spatial-core:publishToMavenCentral",
        ":spatial-geometry:publishToMavenCentral",
        ":spatial-scene:publishToMavenCentral",
        ":spatial-motion:publishToMavenCentral",
        ":spatial-material:publishToMavenCentral",
        ":spatial-light:publishToMavenCentral",
        ":spatial-camera:publishToMavenCentral",
        ":spatial-gesture:publishToMavenCentral",
        ":spatial-renderer:publishToMavenCentral",
        ":spatial-runtime:publishToMavenCentral",
        ":spatial-compose:publishToMavenCentral",
        ":spatial-compose-runtime-adapter:publishToMavenCentral",
        ":spatial:publishToMavenCentral"
    )
}