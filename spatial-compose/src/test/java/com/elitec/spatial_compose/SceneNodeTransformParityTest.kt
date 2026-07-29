package com.elitec.spatial_compose

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_compose.shapes.PrimitiveShape
import com.elitec.spatial_units.meters
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

/** Core #2 item 1.1 — Model and Primitive share the same Modifier3D → modelMatrix path. */
class SceneNodeTransformParityTest {

    @Test
    fun modelAndPrimitiveShareModelMatrixFromSameModifier() {
        val modifier = Modifier3D.Default
            .position(0.5f, -1f, 2f)
            .size(1.5f.meters, 2f.meters, 0.5f.meters)

        val primitive = SceneNode.Primitive(PrimitiveShape.Cube, modifier).toRenderableNode()
        val model = SceneNode.Model("raw:parity", modifier).toRenderableNode()

        assertArrayEquals(primitive.modelMatrix, model.modelMatrix, 1e-5f)
        assertEquals("Cube", primitive.meshId)
        assertEquals("raw:parity", model.meshId)
    }
}
