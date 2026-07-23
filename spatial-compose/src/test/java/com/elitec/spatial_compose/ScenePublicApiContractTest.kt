package com.elitec.spatial_compose

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
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
        val source = scene3DSource() + "\n" + modelResourceSource() + "\n" + rememberModelSource()
        val publicSymbols = Regex("(?m)^(?:@[A-Za-z]+\\s*\\n)*(?:public\\s+)?(?:(?:data|sealed)\\s+)?(?:typealias|class|interface|object|fun)\\s+([A-Z_a-z][A-Za-z0-9_]*)")
            .findAll(source)
            .map { it.groupValues[1] }
            .filterNot { it.first().isLowerCase() && it != "rememberCameraState" && it != "rememberModel" }
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
        return sourcePath.readText()
    }

    // Fase 4 (PLAN_PREDEFINED_3D_MODELS.md:205) follow-up: the "documented compose source api"
    // contract must cover the public symbols exported by `ModelResource.kt` and `rememberModel.kt`
    // in addition to those in `Scene3D.kt`. These files live in the same root package
    // `com.elitec.spatial_compose` and export the post-Core #1 model-loading public API
    // (`ModelResource` sealed interface + `rememberModel` composable). Without scanning them,
    // the contract test would silently allow new public symbols to leak without being audited —
    // the exact failure mode this test exists to prevent.
    private fun modelResourceSource(): String = readRootPackageSource("ModelResource.kt")

    private fun rememberModelSource(): String = readRootPackageSource("rememberModel.kt")

    private fun readRootPackageSource(fileName: String): String {
        val candidates = listOf(
            Path.of("src/main/java/com/elitec/spatial_compose/$fileName"),
            Path.of("spatial-compose/src/main/java/com/elitec/spatial_compose/$fileName"),
        )
        val sourcePath = candidates.firstOrNull { it.exists() }
            ?: error("$fileName not found from ${Path.of("").toAbsolutePath()}")
        return sourcePath.readText()
    }

    private companion object {
        // Fase 4 (PLAN_PREDEFINED_3D_MODELS.md:205): the post-Core #1 model-loading API
        // (`ModelResource` + `rememberModel`) is part of the documented public surface and must
        // remain audited by this contract test, alongside the original Core #1 symbols.
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
            "ModelResource",
            "rememberModel",
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