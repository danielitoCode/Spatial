package io.github.danielitocode.spatial.buildlogic.jvm

import io.github.danielitocode.spatial.buildlogic.constants.Java
import io.github.danielitocode.spatial.buildlogic.constants.Kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class SpatialJvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("java-library")
            
            if (extensions.findByName("kotlin") == null) {
                pluginManager.apply("org.jetbrains.kotlin.jvm")
            }

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = Java.VERSION
                targetCompatibility = Java.VERSION
                toolchain {
                    languageVersion = JavaLanguageVersion.of(Kotlin.JVM_TOOLCHAIN)
                }
            }

            extensions.configure<KotlinJvmProjectExtension> {
                jvmToolchain(Kotlin.JVM_TOOLCHAIN)
                compilerOptions {
                    jvmTarget = Kotlin.JVM_TARGET
                }
            }
        }
    }
}
