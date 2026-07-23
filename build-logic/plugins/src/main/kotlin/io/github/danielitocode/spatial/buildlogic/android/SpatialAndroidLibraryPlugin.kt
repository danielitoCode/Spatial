package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpatialAndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            
            // Verificación más robusta: si ya existe la extensión 'kotlin', no aplicamos el plugin de nuevo
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.android")
            }

            AndroidConfiguration(this).configure()
        }
    }
}
