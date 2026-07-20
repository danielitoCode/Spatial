package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Project

internal class AndroidConfiguration(
    private val project: Project
) {

    fun configure() {

        AndroidLibraryConfiguration(project).configure()

        KotlinConfiguration(project).configure()

        JavaConfiguration(project).configure()

        LintConfiguration(project).configure()

        TestingConfiguration(project).configure()

        // Publishing and Compose are now opt-in via their own plugins
        // to maintain module purity.

        DependencyConfiguration(project).configure()
    }

}