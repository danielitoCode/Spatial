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

private const val TAG = "SpatialModelGraph"

@Composable
internal fun rememberSceneGraph(content: @Composable () -> Unit): List<SceneNode> {
    val builder = remember { SceneBuilder() }
    val scope = remember(builder) { SceneContentScope(builder) }
    CompositionLocalProvider(LocalSceneContentScope provides scope) {
        content()
    }
    val nodes = builder.nodes
    if (nodes.any { it is SceneNode.Model }) {
        Log.i(
            TAG,
            "rememberSceneGraph nodes=${nodes.size} models=${nodes.count { it is SceneNode.Model }} " +
                "ids=${nodes.filterIsInstance<SceneNode.Model>().map { it.meshId }}",
        )
    }
    return nodes
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

    Log.i(
        TAG,
        "ModelSceneElement COMPOSE id=${model.id} rawRes=${model.rawResIdOrNull()} " +
            "scopeOk=${LocalSceneContentScope.current != null}",
    )

    // Load the model asynchronously. While loading, this returns a fallback triangle.
    val mesh = rememberModel(model)
    Log.i(
        TAG,
        "ModelSceneElement after rememberModel id=${model.id} verts=${mesh.vertexCount} idx=${mesh.indexCount}",
    )

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
