package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Java
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class JavaConfiguration(
    private val project: Project
) {

    fun configure() {

        project.extensions.configure<LibraryExtension> {

            compileOptions {

                sourceCompatibility = Java.VERSION

                targetCompatibility = Java.VERSION

            }

        }

    }

}