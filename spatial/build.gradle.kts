plugins {
    alias(libs.plugins.android.library)
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    api(project(":spatial-compose"))
    api(project(":spatial-core"))
    api(project(":spatial-runtime"))
    api(project(":spatial-renderer"))
    api(project(":spatial-camera"))
    api(project(":spatial-geometry"))
    api(project(":spatial-gesture"))
    api(project(":spatial-motion"))
    api(project(":spatial-material"))
    api(project(":spatial-light"))
    api(project(":spatial-units"))
    api(project(":spatial-math"))
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial")
        description.set("Spatial 3D engine")
    }
}
