package com.elitec.spatial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.elitec.spatial.ui.theme.SpatialTheme
import com.elitec.spatial.wiring.RenderWiring
import com.elitec.spatial_renderer.gl.SpatialGlSurfaceView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpatialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpatialRendererHost(modifier = Modifier.fillMaxSize().padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun SpatialRendererHost(modifier: Modifier = Modifier) {
    var cameraText by remember { mutableStateOf("yaw=0.00 pitch=0.00 zoom=1.00") }

    Column(modifier = modifier) {
        Text("Demo gesto -> cámara -> render", modifier = Modifier.padding(12.dp))
        Text(cameraText, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { motionEvent ->
                    val x = motionEvent.x / 1000f
                    val y = motionEvent.y / 1000f
                    RenderWiring.gestureDispatcher.publishOrbit(OrbitGestureDelta(deltaYaw = x, deltaPitch = y))
                    RenderWiring.gestureDispatcher.publishPinch(PinchZoomDelta(scaleDelta = 1.001f))
                    RenderWiring.runtime.requestRenderFrame()
                    val camera = RenderWiring.cameraSnapshot()
                    cameraText = "yaw=${"%.2f".format(camera.yaw)} pitch=${"%.2f".format(camera.pitch)} zoom=${"%.2f".format(camera.zoom)}"
                    true
                },
            factory = { context -> SpatialGlSurfaceView(context) }
        )
    }
}
