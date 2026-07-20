plugins {
    id("spatial.android.library")
    id("spatial.android.library.compose")
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial_compose"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.annotation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(project(":spatial-core"))
    implementation(project(":spatial-geometry"))
    implementation(project(":spatial-gesture"))
    implementation(project(":spatial-camera"))
    implementation(project(":spatial-units"))
    implementation(project(":spatial-motion"))
}

mavenPublishing {
    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-compose",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Compose")
        description.set("Jetpack Compose integration for Spatial")
    }
}
