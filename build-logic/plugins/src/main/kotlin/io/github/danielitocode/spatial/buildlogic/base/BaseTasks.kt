package io.github.danielitocode.spatial.buildlogic.base

import io.github.danielitocode.spatial.buildlogic.core.registerTask

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

internal object BaseTasks {

    fun register(project: Project) {
        project.registerTask<PrintModuleInfoTask>(
            "printModuleInfo"
        )
    }
}

abstract class PrintModuleInfoTask : DefaultTask() {
    @TaskAction
    fun execute() {
        println(
            """
            ==============================
            Module : ${project.name}
            Path   : ${project.path}
            Version: ${project.version}
            ==============================
            """.trimIndent()
        )
    }
}