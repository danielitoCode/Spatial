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

signing {
    useGpgCmd()
}

mavenPublishing {

    publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-core",
        version = "0.1.0-alpha01"
    )

    pom {

        name.set("Spatial Core")

        description.set("Spatial core module")

        inceptionYear.set("2026")

        url.set("https://github.com/danielitoCode/Spatial")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("danielitocode")
                name.set("Daniel")
            }
        }

        scm {
            url.set("https://github.com/danielitoCode/Spatial")
            connection.set("scm:git:git://github.com/danielitoCode/Spatial.git")
            developerConnection.set("scm:git:ssh://github.com/danielitoCode/Spatial.git")
        }
    }
}