plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

dependencies {
    // Motion se apoya en modelo de escena y utilidades matemáticas compartidas.
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-math"))
    implementation(project(":spatial-units"))
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-motion",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Motion")
        description.set("Motion module for Spatial 3D engine")
    }
}
