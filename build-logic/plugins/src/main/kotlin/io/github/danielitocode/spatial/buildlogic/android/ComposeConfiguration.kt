package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

internal class ComposeConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.findByName("android")
        
        when (extension) {
            is LibraryExtension -> {
                extension.buildFeatures {
                    compose = true
                }
            }
            is ApplicationExtension -> {
                extension.buildFeatures {
                    compose = true
                }
            }
        }
    }
}
