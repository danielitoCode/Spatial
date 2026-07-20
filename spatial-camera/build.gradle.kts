plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

dependencies {
    // Camera define comportamiento de vista sobre contratos de escena/core, no sobre renderer directo.
    api(project(":spatial-core"))
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-math"))

    implementation(project(":spatial-motion"))

    testImplementation(libs.junit)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-camera",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Camera")
        description.set("Camera module for Spatial 3D engine")
    }
}
