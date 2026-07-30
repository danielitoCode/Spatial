package com.elitec.spatial_compose.scene

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.elitec.spatial_compose.ModelResource
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.rememberModel
import com.elitec.spatial_compose.shapes.PrimitiveShape
import com.elitec.spatial_geometry.MeshData

private const val TAG = "SpatialModelGraph"

@Composable
internal fun rememberSceneGraph(content: @Composable () -> Unit): List<SceneNode> {
    val builder = remember { SceneBuilder() }
    val scope = remember(builder) { SceneContentScope(builder) }
    CompositionLocalProvider(LocalSceneContentScope provides scope) {
        content()
    }
    // Do NOT log here: autoRotate / camera ticks recompose this every frame.
    return builder.nodes
}

@Composable
internal fun SceneElement(
    shape: PrimitiveShape,
    modifier: Modifier3D = Modifier3D.Default,
) {
    val sceneScope = LocalSceneContentScope.current
        ?: error("Element(...) must be called inside Scene { ... } content.")

    val node = remember(shape, modifier) { SceneNode.Primitive(shape, modifier) }

    DisposableEffect(node) {
        sceneScope.add(node)
        onDispose {
            sceneScope.remove(node)
        }
    }
}

@Composable
internal fun ModelSceneElement(
    model: ModelResource,
    modifier: Modifier3D = Modifier3D.Default,
) {
    val sceneScope = LocalSceneContentScope.current
        ?: error("Element.Model(...) must be called inside Scene { ... } content.")

    val mesh = rememberModel(model)
    // Log only meaningful load transitions (fallback 3 verts → real mesh).
    val loadKey = "${model.id}:${mesh.vertexCount}:${mesh.indexCount}"
    remember(loadKey) {
        Log.i(
            TAG,
            "ModelSceneElement meshReady id=${model.id} verts=${mesh.vertexCount} idx=${mesh.indexCount} " +
                "fallback=${mesh === MeshData.FallbackTriangle} error=${mesh === MeshData.ErrorMesh}",
        )
        true
    }

    val node = remember(model.id, modifier) { SceneNode.Model(model.id, modifier) }

    DisposableEffect(node) {
        Log.i(TAG, "ModelSceneElement ADD node meshId=${node.meshId}")
        sceneScope.add(node)
        onDispose {
            Log.i(TAG, "ModelSceneElement REMOVE node meshId=${node.meshId}")
            sceneScope.remove(node)
        }
    }
}

private val LocalSceneContentScope = compositionLocalOf<SceneContentScope?> { null }
