package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Lint
import org.gradle.api.Project

internal class LintConfiguration(
    private val project: Project
) {

    fun configure() {
        val extension = project.extensions.findByName("android")
        val baselineFile = project.file("lint-baseline.xml")

        when (extension) {
            is LibraryExtension -> {
                extension.lint {
                    abortOnError = Lint.ABORT_ON_ERROR
                    warningsAsErrors = Lint.WARNINGS_AS_ERRORS
                    // Only apply the baseline when the file is already committed in the repo.
                    // This avoids a hard lint failure ("baseline file not found") on modules
                    // that have no pre-existing warnings to suppress.
                    if (baselineFile.exists()) {
                        baseline = baselineFile
                    }
                }
            }
            is ApplicationExtension -> {
                extension.lint {
                    abortOnError = Lint.ABORT_ON_ERROR
                    warningsAsErrors = Lint.WARNINGS_AS_ERRORS
                    if (baselineFile.exists()) {
                        baseline = baselineFile
                    }
                }
            }
        }
    }
}
