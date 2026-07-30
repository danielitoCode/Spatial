package com.elitec.spatial

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.elitec.spatial_geometry.GlobalMeshRegistry
import com.elitec.spatial_geometry.GltfBinaryParser
import com.elitec.spatial_geometry.MeshData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Validates the app-bundled playground asset pipeline up to mesh registration:
 * `R.raw.sample_model` → [GltfBinaryParser] → [GlobalMeshRegistry].
 *
 * Does not assert GPU pixels (that remains a device visual check in Playground).
 *
 * ```
 * ./gradlew :app:connectedDebugAndroidTest
 * ```
 */
@RunWith(AndroidJUnit4::class)
class SampleModelGlbInstrumentedTest {

    private val meshId = "raw:test:sample_model"

    @After
    fun tearDown() {
        GlobalMeshRegistry.unregister(meshId)
    }

    @Test
    fun sample_model_glb_parses_and_registers() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mesh: MeshData = context.resources.openRawResource(R.raw.sample_model).use { stream ->
            GltfBinaryParser.parse(stream)
        }

        assertTrue("sample_model.glb should contain vertices", mesh.vertexCount > 0)
        assertTrue("sample_model.glb should contain indices", mesh.indexCount > 0)
        assertEquals(
            "POSITION length must be 3 * vertexCount",
            mesh.vertexCount * MeshData.CoordinatesPerVertex,
            mesh.vertices.size,
        )
        assertNotEquals(
            "Parsed mesh must not be the empty error placeholder",
            MeshData.ErrorMesh.vertexCount,
            mesh.vertexCount.takeIf { mesh.vertices.contentEquals(MeshData.ErrorMesh.vertices) },
        )

        GlobalMeshRegistry.register(meshId, mesh)
        val fromRegistry = requireNotNull(GlobalMeshRegistry.get(meshId))
        assertEquals(mesh.vertexCount, fromRegistry.vertexCount)
        assertEquals(mesh.indexCount, fromRegistry.indexCount)
    }
}
