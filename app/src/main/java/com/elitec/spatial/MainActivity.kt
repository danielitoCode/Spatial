package com.elitec.spatial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.elitec.spatial_renderer.gl.SpatialGlSurfaceView
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_core.scene.MaterialData
import android.view.MotionEvent
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.elitec.spatial.wiring.RenderWiring
import com.elitec.spatial.ui.theme.SpatialTheme
import com.elitec.spatial_compose.Element
import com.elitec.spatial_compose.Gestures
import com.elitec.spatial_compose.Modifier3D
import com.elitec.spatial_compose.Scene
import com.elitec.spatial_compose.Shapes3D
import com.elitec.spatial_compose.rememberCameraState
import com.elitec.spatial_compose.rememberSceneGraph
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val cameraState = rememberCameraState(yaw = 20f.deg, pitch = (-12f).deg, zoom = 1.25f)
    val scope = rememberCoroutineScope()
    val cameraSnapshot = cameraState.snapshot()
    val cameraText = "yaw=${"%.2f".format(cameraSnapshot.yaw)} " +
            "pitch=${"%.2f".format(cameraSnapshot.pitch)} " +
            "zoom=${"%.2f".format(cameraSnapshot.zoom)}"
    Column(modifier = modifier) {
        Scene(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            cameraState = cameraState,
            gestures = Gestures.orbit(),
        ) {
            Element.Cube(
                modifier = Modifier3D.Default
                    .size(1.4f.meters)
                    .position(0f.meters, 0f.meters, (-4f).meters),
            )
            Element.Sphere(
                modifier = Modifier3D.Default
                    .size(1f.meters)
                    .position(2f.meters, 0f.meters, (-6f).meters),
            )
            Element.Plane(
                modifier = Modifier3D.Default
                    .size(8f.meters, 0.1f.meters, 8f.meters)
                    .position(0f.meters, (-1.2f).meters, (-5f).meters),
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Playground 3D Compose-first", modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        scope.launch {
                            cameraState.animateTo(
                                yaw = (cameraSnapshot.yaw + 90f).deg,
                                pitch = (-18f).deg,
                                zoom = 0.9f,
                            )
                        }
                    },
                ) {
                    Text("Animar cámara")
                }
            }
            Text(cameraText)
            Text("Scene envuelve AndroidView y resuelve el grafo internamente")
        }
    }

}
