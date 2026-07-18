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
    implementation(project(":spatial-core"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-light",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Light")
        description.set("Light module for Spatial 3D engine")
    }
}
