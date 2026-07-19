package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Plugin
import org.gradle.api.Project


class SpatialAndroidLibraryPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        AndroidConfiguration(project)
            .configure()

    }

}