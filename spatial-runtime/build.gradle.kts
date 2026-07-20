plugins {
    id("spatial.android.library")
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial_runtime"
}

dependencies {
    implementation(project(":spatial-core"))
    implementation(project(":spatial-scene"))
    implementation(project(":spatial-camera"))
    implementation(project(":spatial-motion"))
    implementation(project(":spatial-renderer"))
    implementation(project(":spatial-gesture"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-runtime",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Runtime")
        description.set("Runtime module for Spatial 3D engine")
    }
}
