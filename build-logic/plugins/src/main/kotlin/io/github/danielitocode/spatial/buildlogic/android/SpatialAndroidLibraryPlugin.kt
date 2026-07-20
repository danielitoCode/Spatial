package io.github.danielitocode.spatial.buildlogic.android

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Entry point for the `spatial.android.library` plugin ID registered in
 * `build-logic/plugins/build.gradle.kts`.
 *
 * Mirrors [io.github.danielitocode.spatial.buildlogic.base.SpatialBasePlugin]'s pattern: this
 * class only wires the plugin ID to its configuration logic. Requires `com.android.library` to
 * already be applied on the target project (e.g. via `alias(libs.plugins.android.library)`),
 * since [AndroidConfiguration] configures the `LibraryExtension` that plugin registers rather
 * than applying `com.android.library` itself.
 */
class SpatialAndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            // Aplicar plugins base de Android y Kotlin de forma segura
            pluginManager.apply("com.android.library")
            
            // Verificamos si ya existe la extensión 'kotlin' para evitar el error de duplicación
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.android")
            }

            AndroidConfiguration(this).configure()
        }
    }
}
