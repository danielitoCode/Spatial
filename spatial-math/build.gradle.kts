plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-math",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Math")
        description.set("Mathematical foundation module for Spatial 3D engine")
    }
}
