plugins {
    alias(libs.plugins.android.library)
    id("com.vanniktech.maven.publish")
    signing
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

signing {
    useGpgCmd()
}

mavenPublishing {

    publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "io.github.danielitocode",
        artifactId = "spatial-gesture",
        version = "0.1.0-alpha01"
    )

    pom {

        name.set("Spatial Gesture")

        description.set("Gesture module for Spatial 3D engine")

        inceptionYear.set("2026")

        url.set("https://github.com/danielitoCode/Spatial")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("danielitocode")
                name.set("Daniel")
            }
        }

        scm {
            url.set("https://github.com/danielitoCode/Spatial")
            connection.set("scm:git:git://github.com/danielitoCode/Spatial.git")
            developerConnection.set("scm:git:ssh://github.com/danielitoCode/Spatial.git")
        }
    }
}