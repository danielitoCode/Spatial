package io.github.danielitocode.spatial.buildlogic.android

import io.github.danielitocode.spatial.buildlogic.constants.Kotlin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.gradle.kotlin.dsl.configure

internal class KotlinConfiguration(
    private val project: Project
) {

    fun configure() {

        project.extensions.configure<KotlinAndroidProjectExtension> {

            jvmToolchain(
                Kotlin.JVM_TOOLCHAIN
            )

        }

    }

}