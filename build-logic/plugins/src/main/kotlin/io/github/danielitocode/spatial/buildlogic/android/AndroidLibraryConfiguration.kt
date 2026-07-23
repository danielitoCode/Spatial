package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Android
import org.gradle.api.Project

internal class AndroidLibraryConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.findByName("android") ?: return
        
        if (extension is LibraryExtension) {
            extension.compileSdk = Android.COMPILE_SDK
            extension.defaultConfig {
                minSdk = Android.MIN_SDK
                
                // Usamos project.file() que resuelve rutas absolutas correctamente respecto al módulo
                val proguardFile = project.file("consumer-rules.pro")
                if (proguardFile.exists()) {
                    consumerProguardFiles(proguardFile)
                }
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
