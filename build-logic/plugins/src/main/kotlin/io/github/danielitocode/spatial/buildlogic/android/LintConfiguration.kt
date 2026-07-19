package io.github.danielitocode.spatial.buildlogic.android

import com.android.build.api.dsl.LibraryExtension
import io.github.danielitocode.spatial.buildlogic.constants.Lint
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class LintConfiguration(
    private val project: Project
) {

    fun configure() {

        project.extensions.configure<LibraryExtension> {

            lint {

                abortOnError = Lint.ABORT_ON_ERROR

                warningsAsErrors = Lint.WARNINGS_AS_ERRORS

            }

        }

    }

}