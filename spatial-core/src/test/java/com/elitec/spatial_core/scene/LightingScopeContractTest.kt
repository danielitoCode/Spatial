package com.elitec.spatial_core.scene

import com.elitec.spatial_core.render.FrameSnapshot
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LightingScopeContractTest {

    @Test
    fun `core frame snapshot does not transport active lights`() {
        val frameSnapshotProperties = FrameSnapshot::class.java.declaredFields.map { it.name }

        assertFalse(
            "Core #1 must not expose active light transport on FrameSnapshot",
            frameSnapshotProperties.contains("lights"),
        )
        assertFalse(
            "Core #1 must not expose active light state on FrameSnapshot",
            frameSnapshotProperties.contains("lightState"),
        )
    }

    @Test
    fun `readme documents flat color materials and deferred lighting`() {
        val readme = repoRoot().resolve("readme.md").readText()

        assertTrue(readme.contains("Flat-color material rendering (no active lighting/shading in Core #1)"))
        assertTrue(readme.contains("Core #1 keeps lighting as contracts only."))
        assertTrue(readme.contains("does **not** transport lights through the render frame"))
        assertTrue(readme.contains("supports flat-color materials"))
    }

    private fun repoRoot(): Path {
        var current = Path.of(System.getProperty("user.dir")).toAbsolutePath()
        while (!current.resolve("settings.gradle.kts").exists()) {
            current = current.parent ?: error("Unable to locate repository root from ${System.getProperty("user.dir")}")
        }
        return current
    }
}
