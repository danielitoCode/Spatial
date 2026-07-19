package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project

fun Project.applyPlugin(id: String) {
    pluginManager.apply(id)
}

fun Project.hasPlugin(id: String): Boolean {
    return pluginManager.hasPlugin(id)
}