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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }

}
dependencies {
    // Core expone contratos puros. Solo depende de tipos matemáticos y unidades.
    implementation(project(":spatial-math"))
    implementation(project(":spatial-units"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-core",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Core")
        description.set("Spatial core module")
    }
}
