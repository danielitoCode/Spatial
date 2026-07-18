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
    // Scene compone entidades de dominio desde contratos de core.
    implementation(project(":spatial-core"))
    // Dependencias matemáticas estrictas para transformaciones sin acoplar al renderer.
    implementation(project(":spatial-math"))
    implementation(project(":spatial-geometry"))
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-scene",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Scene")
        description.set("Scene module for Spatial 3D engine")
    }
}
