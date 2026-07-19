package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Android
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidLibraryConfiguration(
    private val project: Project
) {

    fun configure() {

        project.extensions.configure<LibraryExtension> {

            compileSdk = Android.COMPILE_SDK

            defaultConfig {

                minSdk = Android.MIN_SDK

                consumerProguardFiles(
                    "consumer-rules.pro"
                )
            }

            buildFeatures {

                buildConfig = true
            }

        }

    }

}