plugins {
    id("spatial.android.library")
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial_renderer"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Renderer solo depende de bloques de bajo nivel (math/material/light), nunca de core/scene/compose.
    implementation(project(":spatial-math"))
    implementation(project(":spatial-geometry"))
    implementation(project(":spatial-material"))
    implementation(project(":spatial-light"))
    // Renderer implementa contratos definidos en core.
    implementation(project(":spatial-core"))
    implementation(project(":spatial-camera"))
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-renderer",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Renderer")
        description.set("Renderer module for Spatial 3D engine")
    }
}
