package com.elitec.spatial_compose

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpatialComposeDependencyContractTest {

    @Test
    fun `spatial compose depends only on allowed framework modules`() {
        val buildFile = spatialComposeBuildFile()
        val source = Files.readString(buildFile)
        val projectDependencies = Regex("project\\(\\\":([^\\\"]+)\\\"\\)")
            .findAll(source)
            .map { it.groupValues[1] }
            .toSet()

        assertEquals(allowedProjectDependencies, projectDependencies)
        assertFalse(
            "spatial-compose must not depend directly on runtime or renderer backends.",
            projectDependencies.any { it in forbiddenBackendDependencies },
        )
        assertTrue(
            "The build file must document Option B as the official framework boundary.",
            source.contains("Frontera oficial Core #1") && source.contains(":spatial-compose-runtime-adapter"),
        )
    }

    private fun spatialComposeBuildFile(): Path {
        val candidates = listOf(
            Path.of("spatial-compose/build.gradle.kts"),
            Path.of("build.gradle.kts"),
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("spatial-compose build.gradle.kts not found from ${Path.of("").toAbsolutePath()}")
    }

    private companion object {
        val allowedProjectDependencies = setOf(
            "spatial-core",
            "spatial-geometry",
            "spatial-gesture",
            "spatial-camera",
            "spatial-units",
            "spatial-motion",
        )
        val forbiddenBackendDependencies = setOf(
            "spatial-renderer",
            "spatial-runtime",
        )
    }
}