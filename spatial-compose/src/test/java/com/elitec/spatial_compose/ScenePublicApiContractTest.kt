package com.elitec.spatial_compose

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenePublicApiContractTest {

    @Test
    fun `scene builder does not keep legacy public shape helpers`() {
        val source = scene3DSource()
        val sceneBuilderBody = Regex(
            "internal class SceneBuilder \\{(?<body>.*?)\\n\\}",
            RegexOption.DOT_MATCHES_ALL,
        ).find(source)?.groups?.get("body")?.value.orEmpty()

        assertFalse(Regex("\\bfun\\s+(cube|sphere|plane|element)\\s*\\(").containsMatchIn(sceneBuilderBody))
    }


    @Test
    fun `documented compose source api stays intentionally small`() {
        val source = scene3DSource()
        val publicSymbols = Regex("(?m)^(?:@[A-Za-z]+\\s*\\n)*(?:public\\s+)?(?:(?:data|sealed)\\s+)?(?:typealias|class|interface|object|fun)\\s+([A-Z_a-z][A-Za-z0-9_]*)")
            .findAll(source)
            .map { it.groupValues[1] }
            .filterNot { it.first().isLowerCase() && it != "rememberCameraState" }
            .filterNot { it in internalSymbols }
            .toSet()

        assertEquals(expectedPublicSymbols, publicSymbols)
    }

    private fun scene3DSource(): String {
        val candidates = listOf(
            Path.of("src/main/java/com/elitec/spatial_compose/Scene3D.kt"),
            Path.of("spatial-compose/src/main/java/com/elitec/spatial_compose/Scene3D.kt"),
        )
        val sourcePath = candidates.firstOrNull { it.exists() }
            ?: error("Scene3D.kt not found from ${Path.of("").toAbsolutePath()}")
        return Files.readString(sourcePath)
    }

    private companion object {
        val expectedPublicSymbols = setOf(
            "Modifier3D",
            "CameraState",
            "MotionSpec",
            "GestureSensitivity",
            "SceneRenderHostFactory",
            "SceneGestures",
            "Gestures",
            "Element",
            "Scene",
            "rememberCameraState",
        )
        val internalSymbols = setOf(
            "Vec3Distance",
            "Rotation3D",
            "Shapes3D",
            "SceneRenderHost",
            "DefaultSceneRenderHostFactory",
            "SpatialRuntimeSceneRenderHost",
            "SceneRenderHostHolder",
            "ComposeFrameCameraAnimationScheduler",
            "SceneNode",
            "PrimitiveShape",
            "SceneBuilder",
            "SceneContentScope",
            "LocalSceneContentScope",
            "SceneElement",
            "PointerPosition",
            "OrbitGestureDeltaPixels",
            "RawSceneGestureDelta",
            "SceneGestureInputState",
            "OrbitGestureDeltaDegrees",
        )
    }
}