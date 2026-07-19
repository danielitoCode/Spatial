package io.github.danielitocode.spatial.buildlogic.utils

import org.gradle.api.Project

fun Project.log(message: String) {
    logger.lifecycle("[Spatial] $message")
}