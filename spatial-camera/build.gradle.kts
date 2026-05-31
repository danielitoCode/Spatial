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
    // Camera define comportamiento de vista sobre contratos de escena/core, no sobre renderer directo.
    api(project(":spatial-core"))
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-math"))

    implementation(project(":spatial-motion"))

    testImplementation(libs.junit)
}
