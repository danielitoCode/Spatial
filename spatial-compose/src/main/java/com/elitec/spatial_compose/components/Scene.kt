package com.elitec.spatial_compose.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.elitec.spatial_compose.BuildConfig
import com.elitec.spatial_compose.modifier.sceneGestureInput
import com.elitec.spatial_compose.scene.Gestures
import com.elitec.spatial_compose.scene.SceneGestures
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.SceneRenderHostFactory
import com.elitec.spatial_compose.scene.SceneRenderHostHolder
import com.elitec.spatial_compose.scene.rememberSceneGraph
import com.elitec.spatial_compose.scene.renderSceneFrame
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_compose.state.CameraState
import com.elitec.spatial_compose.state.rememberCameraState

@Composable
fun Scene(
    modifier: Modifier = Modifier,
    renderHostFactory: SceneRenderHostFactory,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    content: @Composable () -> Unit,
) {
    val sceneNodes = rememberSceneGraph(content)
    val renderableNodes = sceneNodes.map(SceneNode::toRenderableNode)
    if (BuildConfig.DEBUG) {
        Log.d(
            TAG,
            "renderableNodes: sceneNodes.size=${sceneNodes.size}, meshIds=${renderableNodes.map { it.meshId }}",
        )
    }
    val cameraSnapshot = cameraState.snapshot()
    val renderHostHolder = remember { SceneRenderHostHolder() }

    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val clearColor = remember(backgroundColor) { backgroundColor.toColor4() }

    // Forward Activity pause/resume to GLSurfaceView so background → foreground recreates cleanly.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, renderHostHolder) {
        val observer = LifecycleEventObserver { _, event ->
            val host = renderHostHolder.host ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_PAUSE -> host.onPause()
                Lifecycle.Event.ON_RESUME -> host.onResume()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier
            .onSizeChanged { viewportSize = it }
            .sceneGestureInput(cameraState, gestures, sceneNodes, viewportSize),
        factory = { context ->
            renderHostFactory.create(context).also { host ->
                renderHostHolder.host = host
                // Device-closure task 1.2: on the first composition, Element DisposableEffects have
                // not yet added nodes to the SnapshotStateList, so renderableNodes is often empty
                // here. Enqueueing that empty frame only lengthens a clear-only first paint.
                // SideEffect / update below push the real scene once nodes exist; the host still
                // queues safely if GL is not ready.
                if (renderableNodes.isNotEmpty()) {
                    host.renderSceneFrame(renderableNodes, cameraSnapshot, clearColor)
                }
            }.view
        },
        onRelease = {
            renderHostHolder.dispose()
        },
        update = {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot, clearColor)
        },
    )

    // After effects apply SceneElement nodes, this composition pass has a non-empty graph.
    // Guarantee a frame request even if AndroidView's update block is skipped in edge cases.
    SideEffect {
        if (renderableNodes.isNotEmpty()) {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot, clearColor)
        }
    }
}

private fun androidx.compose.ui.graphics.Color.toColor4(): com.elitec.spatial_core.render.Color4 {
    return com.elitec.spatial_core.render.Color4(red, green, blue, alpha)
}

private const val TAG = "SpatialComposeScene"
