package io.github.danielitocode.spatial.buildlogic.android

import io.github.danielitocode.spatial.buildlogic.android.internal.CompileConfiguration
import io.github.danielitocode.spatial.buildlogic.android.internal.KotlinConfiguration
import io.github.danielitocode.spatial.buildlogic.core.applyPlugin
import io.github.danielitocode.spatial.buildlogic.core.log
import org.gradle.api.Project


internal class AndroidConfiguration(
    private val project: Project
) {


    fun configure() {

        applyPlugins()

        configureAndroid()

        configureKotlin()

        project.log(
            "Android Library configured"
        )

    }


    private fun applyPlugins() {

        project.applyPlugin(
            "com.android.library"
        )

        project.applyPlugin(
            "org.jetbrains.kotlin.android"
        )

    }


    private fun configureAndroid() {

        CompileConfiguration(project)
            .configure()

    }


    private fun configureKotlin() {

        KotlinConfiguration(project)
            .configure()

    }

}