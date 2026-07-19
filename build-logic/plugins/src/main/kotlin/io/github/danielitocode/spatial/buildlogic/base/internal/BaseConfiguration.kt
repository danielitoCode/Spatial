package io.github.danielitocode.spatial.buildlogic.base.internal

import io.github.danielitocode.spatial.buildlogic.base.BaseExtension
import io.github.danielitocode.spatial.buildlogic.base.BaseTasks
import org.gradle.api.Project

internal class BaseConfiguration(
    private val project: Project
) {

    fun configure() {

        createExtension()

        registerTasks()

        project.logger.lifecycle(
            "Spatial Build Logic initialized for ${project.path}"
        )
    }

    private fun createExtension() {
        project.extensions.create(
            "spatial",
            BaseExtension::class.java
        )
    }

    private fun registerTasks() {
        BaseTasks.register(project)
    }

}