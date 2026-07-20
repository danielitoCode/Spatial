plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-units",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Units")
        description.set("Units module for Spatial 3D engine")
    }
}
