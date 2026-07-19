package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project

internal val Project.isRootProject: Boolean
    get() = this == rootProject

internal val Project.moduleName: String
    get() = name

internal val Project.modulePath: String
    get() = path

internal val Project.groupId: String
    get() = group.toString()

internal val Project.versionName: String
    get() = version.toString()