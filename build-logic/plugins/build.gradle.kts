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
    implementation("com.android.tools.build:gradle:9.2.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
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

        register("spatialAndroidLibraryCompose") {

            id = "spatial.android.library.compose"

            implementationClass =
                "io.github.danielitocode.spatial.buildlogic.android.SpatialAndroidLibraryComposePlugin"

            displayName = "Spatial Android Library Compose Plugin"

            description =
                "Enables Jetpack Compose for Spatial Android library modules"

        }

        register("spatialPublish") {

            id = "spatial.publish"

            implementationClass =
                "io.github.danielitocode.spatial.buildlogic.publish.SpatialPublishPlugin"

            displayName = "Spatial Publication Plugin"

            description = "Handles Maven Central publication and GPG signing for Spatial modules."
        }

        register("spatialJvmLibrary") {

            id = "spatial.jvm.library"

            implementationClass =
                "io.github.danielitocode.spatial.buildlogic.jvm.SpatialJvmLibraryPlugin"

            displayName = "Spatial JVM Library Plugin"

            description = "Convention plugin for Spatial pure Kotlin/JVM library modules."
        }
    }
}