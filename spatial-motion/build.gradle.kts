plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
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