package io.github.danielitocode.spatial.buildlogic.core

import org.gradle.api.Project
import org.gradle.util.GradleVersion

internal object SpatialGradle {

    val minimumGradle =
        GradleVersion.version("9.0")

    fun verify(project: Project) {

        val current = GradleVersion.current()

        require(current >= minimumGradle) {
            """
            Spatial Build Logic requiere Gradle ${minimumGradle.version}
            Gradle actual: ${current.version}
            """.trimIndent()
        }

    }

}