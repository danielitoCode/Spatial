plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-material",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Material")
        description.set("Material module for compatibility from Spatial 3D engine and Material Design styles")
    }
}
