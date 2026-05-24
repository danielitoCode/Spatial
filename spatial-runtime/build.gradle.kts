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
    implementation(project(":spatial-core"))
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-camera"))
    implementation(project(":spatial-motion"))
    implementation(project(":spatial-renderer"))
    implementation(project(":spatial-gesture"))
}