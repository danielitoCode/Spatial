package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Android
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType

internal class AndroidLibraryConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.findByName("android")
        
        if (extension is LibraryExtension) {
            extension.compileSdk = Android.COMPILE_SDK
            extension.defaultConfig {
                minSdk = Android.MIN_SDK
                consumerProguardFiles("consumer-rules.pro")
            }
            extension.buildFeatures {
                buildConfig = true
            }
        } else if (extension is ApplicationExtension) {
            extension.compileSdk = Android.COMPILE_SDK
            extension.defaultConfig {
                minSdk = Android.MIN_SDK
                targetSdk = Android.TARGET_SDK
            }
            extension.buildFeatures {
                buildConfig = true
            }
        }
    }
}
