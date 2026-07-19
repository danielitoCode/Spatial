package io.github.danielitocode.spatial.buildlogic.base.internal

import io.github.danielitocode.spatial.buildlogic.base.SpatialExtension
import io.github.danielitocode.spatial.buildlogic.core.log
import org.gradle.api.Project

internal class BaseConfiguration(
    private val project: Project
) {

    fun configure() {

        registerExtension()

        project.log("Spatial Base Plugin loaded")

    }

    private fun registerExtension() {

        project.extensions.create(
            "spatial",
            SpatialExtension::class.java
        )

    }

}