package com.elitec.spatial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.elitec.spatial.ui.theme.SpatialTheme
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
    AndroidView(
        modifier = modifier,
        factory = { context -> SpatialGlSurfaceView(context) }
    )
}
