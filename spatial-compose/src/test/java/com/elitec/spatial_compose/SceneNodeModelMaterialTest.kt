package com.elitec.spatial_compose

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_core.scene.MaterialData
import org.junit.Assert.assertEquals
import org.junit.Test

class SceneNodeModelMaterialTest {

    @Test
    fun `model renderable uses modifier material override`() {
        val material = MaterialData(r = 0.1f, g = 0.2f, b = 0.3f, a = 0.4f)
        val node = SceneNode.Model(
            meshId = "model.glb",
            modifier = Modifier3D.Default.material(material),
        )

        val renderableNode = node.toRenderableNode()

        assertEquals(material, renderableNode.material)
    }
}