package com.elitec.spatial_compose.components

import android.content.Context
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
import com.elitec.spatial_camera.camera.SpatialCamera
import com.elitec.spatial_compose.modifier.sceneGestureInput
import com.elitec.spatial_compose.scene.Gestures
import com.elitec.spatial_compose.scene.SceneGestures
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.scene.SceneRenderHost
import com.elitec.spatial_compose.scene.SceneRenderHostFactory
import com.elitec.spatial_compose.scene.SceneRenderHostHolder
import com.elitec.spatial_compose.scene.rememberSceneGraph
import com.elitec.spatial_compose.scene.renderSceneFrame
import com.elitec.spatial_compose.scene.toRenderableNode
import com.elitec.spatial_compose.state.CameraState
import com.elitec.spatial_compose.state.rememberCameraState
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.gl.SpatialGlRenderTarget
import com.elitec.spatial_runtime.SpatialRuntime

@Composable
fun Scene(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    content: @Composable () -> Unit,
) {
    Scene(
        modifier = modifier,
        cameraState = cameraState,
        gestures = gestures,
        renderHostFactory = DefaultSceneRenderHostFactory,
        content = content,
    )
}

@Composable
internal fun Scene(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    renderHostFactory: SceneRenderHostFactory = DefaultSceneRenderHostFactory,
    content: @Composable () -> Unit,
) {
    val sceneNodes = rememberSceneGraph(content)
    val renderableNodes = sceneNodes.map(SceneNode::toRenderableNode)
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
                host.renderSceneFrame(renderableNodes, cameraSnapshot)
            }.view
        },
        update = {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot)
        },
    )
}

private object DefaultSceneRenderHostFactory : SceneRenderHostFactory {
    override fun create(context: Context): SceneRenderHost = SpatialRuntimeSceneRenderHost(context)
}

private class SpatialRuntimeSceneRenderHost(context: Context) : SceneRenderHost {
    private val renderTarget = SpatialGlRenderTarget(context)
    private val runtimeCamera = SpatialCamera()
    private val runtime = SpatialRuntime(
        renderBackend = renderTarget,
        frameScheduler = ChoreographerFrameScheduler(),
        cameraRuntime = runtimeCamera,
    )
    private var pendingNodes: List<RenderableNode> = emptyList()
    private var pendingCameraSnapshot: CameraSnapshot = runtimeCamera.snapshot()

    override val view: View get() = renderTarget.view

    init {
        runtime.onInitialize()
    }

    override fun updateScene(nodes: List<RenderableNode>) {
        pendingNodes = nodes
    }

    override fun updateCamera(cameraSnapshot: CameraSnapshot) {
        pendingCameraSnapshot = cameraSnapshot
    }

    override fun requestFrame() {
        runtime.requestFrame(
            nodes = pendingNodes,
            cameraSnapshot = pendingCameraSnapshot,
        )
    }
}
