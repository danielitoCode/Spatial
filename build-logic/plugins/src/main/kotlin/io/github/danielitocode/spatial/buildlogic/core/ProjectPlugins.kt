package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project

internal fun Project.applyPlugin(id: String) {

    if (!pluginManager.hasPlugin(id)) {
        pluginManager.apply(id)
    }

}

internal fun Project.hasPlugin(id: String): Boolean =
    pluginManager.hasPlugin(id)