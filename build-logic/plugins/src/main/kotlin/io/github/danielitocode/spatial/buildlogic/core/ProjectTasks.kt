package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

inline fun <reified T : Task> Project.registerTask(
    name: String,
    noinline configuration: T.() -> Unit = {}
): TaskProvider<T> =
    tasks.register(name, T::class.java) {
        configuration()
    }