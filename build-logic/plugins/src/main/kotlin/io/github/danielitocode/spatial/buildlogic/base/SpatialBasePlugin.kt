package io.github.danielitocode.spatial.buildlogic.base

import io.github.danielitocode.spatial.buildlogic.base.internal.BaseConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

class SpatialBasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        BaseConfiguration(project).configure()
    }

}