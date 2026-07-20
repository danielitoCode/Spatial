plugins {
    id("spatial.jvm.library")
    id("spatial.publish")
}

dependencies {
    // Scene compone entidades de dominio desde contratos de core.
    implementation(project(":spatial-core"))
    // Dependencias matemáticas estrictas para transformaciones sin acoplar al renderer.
    implementation(project(":spatial-math"))
    implementation(project(":spatial-geometry"))
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-scene",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Scene")
        description.set("Scene module for Spatial 3D engine")
    }
}
