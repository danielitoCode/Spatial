package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpatialAndroidLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            ComposeConfiguration(this).configure()
        }
    }
}
