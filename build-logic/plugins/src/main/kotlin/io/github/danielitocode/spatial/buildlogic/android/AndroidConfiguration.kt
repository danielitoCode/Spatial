package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Project

internal class AndroidConfiguration(
    private val project: Project
) {

    fun configure() {

        AndroidLibraryConfiguration(project).configure()

        KotlinConfiguration(project).configure()

        JavaConfiguration(project).configure()

        ComposeConfiguration(project).configure()

        LintConfiguration(project).configure()

        TestingConfiguration(project).configure()

        PublishingConfiguration(project).configure()

        DependencyConfiguration(project).configure()
    }

}