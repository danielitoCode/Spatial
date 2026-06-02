package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.shapes.PrimitiveShape

@Composable
internal fun rememberSceneGraph(content: @Composable () -> Unit): List<SceneNode> {
    val scope = remember { SceneContentScope(SceneBuilder()) }
    scope.reset()
    CompositionLocalProvider(LocalSceneContentScope provides scope) {
        content()
    }
    return scope.build()
}

@Composable
internal fun SceneElement(
    shape: PrimitiveShape,
    modifier: Modifier3D = Modifier3D.Default,
) {
    val sceneScope = LocalSceneContentScope.current
        ?: error("Element(...) must be called inside Scene { ... } content.")
    sceneScope.add(shape, modifier)
}

private val LocalSceneContentScope = compositionLocalOf<SceneContentScope?> { null }