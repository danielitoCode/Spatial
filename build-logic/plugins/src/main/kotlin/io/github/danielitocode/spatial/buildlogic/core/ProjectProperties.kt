package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project

fun Project.stringProperty(name: String): String? =
    findProperty(name)?.toString()

fun Project.booleanProperty(
    name: String,
    default: Boolean = false
): Boolean =
    stringProperty(name)?.toBoolean() ?: default

fun Project.intProperty(
    name: String,
    default: Int = 0
): Int =
    stringProperty(name)?.toIntOrNull() ?: default