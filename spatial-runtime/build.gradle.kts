plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.elitec.spatial_runtime"
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