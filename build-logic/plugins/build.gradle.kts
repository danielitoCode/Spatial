import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.`java-gradle-plugin`
import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "io.github.danielitocode"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.12.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
}

gradlePlugin {

    plugins {

        register("spatialBase") {

            id = "spatial.base"

            implementationClass =
                "io.github.danielitocode.spatial.buildlogic.base.SpatialBasePlugin"

            displayName = "Spatial Base Plugin"

            description = "Base infrastructure shared by all Spatial Gradle plugins."
        }
    }
}

gradlePlugin {

    plugins {

        register("spatialAndroidLibrary") {

            id = "spatial.android.library"

            implementationClass =
                "io.github.danielitocode.spatial.buildlogic.android.SpatialAndroidLibraryPlugin"

            displayName = "Spatial Android Library Plugin"

            description =
                "Convention plugin for Spatial Android library modules"

        }

    }
}