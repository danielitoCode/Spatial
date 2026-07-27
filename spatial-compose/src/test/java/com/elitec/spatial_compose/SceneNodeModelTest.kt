package com.elitec.spatial_compose

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_units.meters
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SceneNodeModelTest {
    @Test
    fun modelToRenderableNodePreservesMeshIdModelMatrixAndDefaultMaterial() {
        val meshId = "raw:fixture_cube"
        val modifier = Modifier3D.Default
            .position(1f, 2f, 3f)
            .size(2f.meters, 3f.meters, 4f.meters)

        val renderable = SceneNode.Model(meshId, modifier).toRenderableNode()

        assertEquals(meshId, renderable.meshId)
        assertArrayEquals(
            floatArrayOf(
                2f, 0f, 0f, 0f,
                0f, 3f, 0f, 0f,
                0f, 0f, 4f, 0f,
                1f, 2f, 3f, 1f,
            ),
            renderable.modelMatrix,
            1e-5f,
        )
        assertEquals(MaterialData(r = 1f, g = 1f, b = 1f, a = 1f), renderable.material)
    }
}