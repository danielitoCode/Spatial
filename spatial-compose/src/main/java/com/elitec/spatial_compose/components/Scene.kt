package com.elitec.spatial_compose.components

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
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

    AndroidView(
        modifier = modifier
            .onSizeChanged { viewportSize = it }
            .sceneGestureInput(cameraState, gestures, sceneNodes, viewportSize),
        factory = { context ->
            renderHostFactory.create(context).also { host ->
                renderHostHolder.host = host
                // Safe even though the GL surface is not guaranteed ready yet: the render host
                // (SpatialRuntimeSceneRenderHost) queues this first frame internally and replays it
                // once `onSurfaceCreated` fires, instead of touching GL before it's ready. See the
                // item 1.2 audit notes in CORE1_STABILITY.md for the full mechanism and its
                // known limitations (single-slot coalescer, not a real queue).
                host.renderSceneFrame(renderableNodes, cameraSnapshot)
            }.view
        },
        onRelease = {
            renderHostHolder.dispose()
        },
        update = {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot)
        },
    )
}

private const val TAG = "SpatialComposeScene"