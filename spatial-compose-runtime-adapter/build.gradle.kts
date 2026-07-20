plugins {
    alias(libs.plugins.android.library)
    id("spatial.publish")
}

android {
    namespace = "com.elitec.spatial_compose_runtime_adapter"
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

    implementation(project(":spatial-compose"))
    implementation(project(":spatial-core"))
    implementation(project(":spatial-camera"))
    implementation(project(":spatial-renderer"))
    implementation(project(":spatial-runtime"))
}

mavenPublishing {

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-runtime-adapter",
        version = project.version.toString()
    )

    pom {
        name.set("Spatial Runtime Adapter")
        description.set("Runtime Adapter module for Spatial")
    }
}
