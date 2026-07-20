package io.github.danielitocode.spatial.buildlogic.jvm

import io.github.danielitocode.spatial.buildlogic.constants.Java
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class SpatialJvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("java-library")
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = Java.VERSION
                targetCompatibility = Java.VERSION
            }

            extensions.configure<KotlinJvmProjectExtension> {
                jvmToolchain(io.github.danielitocode.spatial.buildlogic.constants.Kotlin.JVM_TOOLCHAIN)
            }
        }
    }
}
