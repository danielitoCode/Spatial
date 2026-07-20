plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
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
