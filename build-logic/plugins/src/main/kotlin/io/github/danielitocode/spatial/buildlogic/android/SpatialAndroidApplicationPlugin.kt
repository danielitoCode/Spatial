package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpatialAndroidApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.android")
            }

            AndroidConfiguration(this).configure()
        }
    }
}
