package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project

private const val PREFIX = "[Spatial]"

fun Project.log(message: String) =
    logger.lifecycle("$PREFIX $message")

fun Project.warn(message: String) =
    logger.warn("$PREFIX $message")

fun Project.error(message: String) =
    logger.error("$PREFIX $message")