package com.elitec.spatial_compose

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_geometry.GlobalMeshRegistry
import com.elitec.spatial_geometry.MeshData
import com.elitec.spatial_material.PbrMaterial
import com.elitec.spatial_material.UnlitMaterial
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class SceneNodeModelMaterialTest {

    @After
    fun tearDown() {
        GlobalMeshRegistry.clear()
    }

    @Test
    fun `model renderable uses public pbr material override`() {
        val material = PbrMaterial(
            r = 0.1f,
            g = 0.2f,
            b = 0.3f,
            a = 0.4f,
            metallic = 0.8f,
            roughness = 0.2f,
        )
        val node = SceneNode.Model(
            meshId = "model.glb",
            modifier = Modifier3D.Default.material(material),
        )

        val renderableNode = node.toRenderableNode()

        assertEquals(
            MaterialData(
                r = 0.1f,
                g = 0.2f,
                b = 0.3f,
                a = 0.4f,
                metallicFactor = 0.8f,
                roughnessFactor = 0.2f
            ),
            renderableNode.material
        )
    }

    @Test
    fun `model renderable uses public unlit material override`() {
        val material = UnlitMaterial(r = 0.5f, g = 0.6f, b = 0.7f, a = 0.8f)
        val node = SceneNode.Model(
            meshId = "model.glb",
            modifier = Modifier3D.Default.material(material),
        )

        val renderableNode = node.toRenderableNode()

        assertEquals(MaterialData(r = 0.5f, g = 0.6f, b = 0.7f, a = 0.8f), renderableNode.material)
    }

    @Test
    fun `model renderable inherits material from mesh registry when no override is provided`() {
        // Given
        val testMeshId = "inherited_material_test.glb"
        val expectedMaterial = MaterialData(
            r = 0.2f,
            g = 0.4f,
            b = 0.6f,
            a = 0.8f,
            metallicFactor = 0.3f
        )
        GlobalMeshRegistry.register(
            meshId = testMeshId,
            meshData = MeshData.FallbackTriangle.copy(material = expectedMaterial),
        )

        // When
        val renderableNode = SceneNode.Model(meshId = testMeshId).toRenderableNode()

        // Then
        assertEquals(
            "The renderable node should use the material defined in GlobalMeshRegistry by default",
            expectedMaterial,
            renderableNode.material
        )
    }

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