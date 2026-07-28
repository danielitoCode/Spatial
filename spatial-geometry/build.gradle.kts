plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

dependencies {
    api(project(":spatial-core"))
    testImplementation(libs.junit)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-geometry",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Geometry")
        description.set("Geometry module for Spatial 3D engine")
    }
}
