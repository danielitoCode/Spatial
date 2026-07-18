plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("com.vanniktech.maven.publish")
    signing
}

android {
    namespace = "com.elitec.spatial_compose"
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
        compose = true
        buildConfig = true
    }

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

    // Frontera oficial Core #1: Compose consume únicamente contratos de core para evitar acoplar UI a backends de bajo nivel.
    // Option B: :spatial-compose-runtime-adapter actúa como puente entre Compose y el runtime/backend de renderizado.
    //   La app depende de :spatial-compose-runtime-adapter; este módulo no declara esa dependencia para mantener la frontera.
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
