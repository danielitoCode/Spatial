package com.elitec.spatial_compose.components

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

    // Track 1 (Fix background-then-foreground bug, Core #1): observe the Activity/Fragment
    // `LifecycleOwner` and forward ON_PAUSE/ON_RESUME to the render host. Without this, the
    // `GLSurfaceView` is never told to pause its GL thread when the app is backgrounded, and on
    // return the EGL context is in an undefined state - on devices that don't preserve the EGL
    // context across pause, `onSurfaceCreated` does not reliably re-fire, and even when it does,
    // no one re-pushes the still-cached `pendingNodes` to the renderer, so the 3D figures stay
    // invisible until the user touches a slider (the bug being fixed here).
    //
    // We register on `DisposableEffect(renderHostHolder)` rather than via `LifecycleStartEffect`
    // because we need this to settle BEFORE the AndroidView's `factory` runs (which creates the
    // host) - and `factory` is not a hook Compose lets us run-after-our-DisposableEffect reliably
    // unless we both hold the host reference and also subscribe BEFORE onCreate/RESUMED passes
    // through. The implementation here keeps the subscription tied to the host lifetime, so the
    // observer is removed when the host is disposed and never leaks across recompositions.
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
                // Safe even though the GL surface is not guaranteed ready yet: the render host
                // (SpatialRuntimeSceneRenderHost) queues this first frame internally and replays it
                // once `onSurfaceCreated` fires, instead of touching GL before it's ready. See the
                // item 1.2 audit notes in CORE1_STABILITY.md for the full mechanism and its
                // known limitations (single-slot coalescer, not a real queue).
                host.renderSceneFrame(renderableNodes, cameraSnapshot, clearColor)
            }.view
        },
        onRelease = {
            renderHostHolder.dispose()
        },
        update = {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot, clearColor)
        },
    )
}

private fun androidx.compose.ui.graphics.Color.toColor4(): com.elitec.spatial_core.render.Color4 {
    return com.elitec.spatial_core.render.Color4(red, green, blue, alpha)
}

private const val TAG = "SpatialComposeScene"