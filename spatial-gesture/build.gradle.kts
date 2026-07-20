plugins {
    alias(libs.plugins.android.library)
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial_gesture"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(project(":spatial-core"))
    implementation(project(":spatial-camera"))
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
        artifactId = "spatial-gesture",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Gesture")
        description.set("Gesture module for Spatial 3D engine")
    }
}
