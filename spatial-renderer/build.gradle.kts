plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.elitec.spatial_renderer"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Renderer solo depende de bloques de bajo nivel (math/material/light), nunca de core/scene/compose.
    implementation(project(":spatial-math"))
    implementation(project(":spatial-material"))
    implementation(project(":spatial-light"))
    // Renderer implementa contratos definidos en core.
    implementation(project(":spatial-core"))
}