plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("com.vanniktech.maven.publish")
    signing
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    // Motion se apoya en modelo de escena y utilidades matemáticas compartidas.
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-math"))
    implementation(project(":spatial-units"))
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-motion",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Motion")
        description.set("Motion module for Spatial 3D engine")
    }
}
