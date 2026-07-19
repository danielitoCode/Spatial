package io.github.danielitocode.spatial.buildlogic.base.internal

import io.github.danielitocode.spatial.buildlogic.base.SpatialExtension
import org.gradle.api.Project

internal class BaseConfiguration(
    private val project: Project
) {

    fun configure() {

        registerExtension()

        configureLogging()

    }

    private fun registerExtension() {

        project.extensions.create(
            "spatial",
            SpatialExtension::class.java
        )

    }

    private fun configureLogging() {

        project.logger.lifecycle(
            "✔ Spatial Base Plugin loaded for ${project.path}"
        )

    }

}