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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elitec.spatial.ui.theme.SpatialTheme
import com.elitec.spatial_compose.Modifier3D
import com.elitec.spatial_compose.rememberSceneGraph
import com.elitec.spatial_units.meters

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

    val sceneNodes = rememberSceneGraph {
        cube(Modifier3D.Default.position(0f.meters, 0f.meters, (-4f).meters).size(1.4f.meters))
        sphere(Modifier3D.Default.position(2f.meters, 0f.meters, (-6f).meters).size(1f.meters))
        plane(Modifier3D.Default.position(0f.meters, (-1.2f).meters, (-5f).meters).size(8f.meters, 0.1f.meters, 8f.meters))
    }

    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("Playground básico: triángulo GL + estado de cámara")
        Text(cameraText)
        Text("Scene DSL activa: ${sceneNodes.joinToString { it.shape.name }}")
    }
}
