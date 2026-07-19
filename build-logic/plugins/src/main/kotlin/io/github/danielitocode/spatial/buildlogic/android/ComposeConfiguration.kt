package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class ComposeConfiguration(
    private val project: Project
) {

    fun configure() {

        project.extensions.configure<LibraryExtension> {

            buildFeatures {

                compose = true
            }

        }

    }

}