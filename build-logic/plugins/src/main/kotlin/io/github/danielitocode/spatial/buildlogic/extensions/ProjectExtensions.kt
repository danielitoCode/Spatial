package io.github.danielitocode.spatial.buildlogic.extensions

import org.gradle.api.Project

val Project.isRootModule: Boolean
    get() = this == rootProject

val Project.moduleName: String
    get() = name