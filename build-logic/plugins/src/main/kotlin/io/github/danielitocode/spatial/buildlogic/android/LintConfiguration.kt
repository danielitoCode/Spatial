package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Lint
import org.gradle.api.Project

internal class LintConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.getByName("android")
        
        when (extension) {
            is LibraryExtension -> {
                extension.lint {
                    abortOnError = Lint.ABORT_ON_ERROR
                    warningsAsErrors = Lint.WARNINGS_AS_ERRORS
                }
            }
            is ApplicationExtension -> {
                extension.lint {
                    abortOnError = Lint.ABORT_ON_ERROR
                    warningsAsErrors = Lint.WARNINGS_AS_ERRORS
                }
            }
        }
    }
}
