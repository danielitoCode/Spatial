plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
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
