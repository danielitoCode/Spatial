package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Java
import org.gradle.api.Project

internal class JavaConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.getByName("android")
        
        when (extension) {
            is LibraryExtension -> {
                extension.compileOptions {
                    sourceCompatibility = Java.VERSION
                    targetCompatibility = Java.VERSION
                }
            }
            is ApplicationExtension -> {
                extension.compileOptions {
                    sourceCompatibility = Java.VERSION
                    targetCompatibility = Java.VERSION
                }
            }
        }
    }
}
