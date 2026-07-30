package com.elitec.spatial.presentation.feature.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elitec.spatial.R
import com.elitec.spatial_compose.Element
import com.elitec.spatial_compose.Gestures
import com.elitec.spatial_compose.ModelResource
import com.elitec.spatial_compose.Modifier3D
import com.elitec.spatial_compose.Scene
import com.elitec.spatial_compose.rememberCameraState
import com.elitec.spatial_compose_runtime_adapter.DefaultSceneRenderHostFactory
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters
import kotlinx.coroutines.launch

/**
 * Interactive Core #1 / Core #2 sample scene.
 *
 * Loads [R.raw.sample_model] via [ModelResource.fromRawResource] → [Element.Model]
 * (rememberModel + GltfBinaryParser + GlobalMeshRegistry JIT upload).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(modifier: Modifier = Modifier) {
    val cameraState = rememberCameraState(yaw = 25f.deg, pitch = (-15f).deg, zoom = 1.25f)
    val scope = rememberCoroutineScope()

    var autoRotate by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }
    var primitiveCount by remember { mutableStateOf(3) }
    // Default ON so sample_model.glb is visible without hunting for the toggle.
    var showModel by remember { mutableStateOf(true) }

    var yawSlider by remember { mutableStateOf(25f) }
    var pitchSlider by remember { mutableStateOf(-15f) }
    var zoomSlider by remember { mutableStateOf(1.25f) }

    val cameraSnapshot = cameraState.snapshot()
    val cameraText = "yaw=${"%.2f".format(cameraSnapshot.yaw)}  " +
        "pitch=${"%.2f".format(cameraSnapshot.pitch)}  " +
        "zoom=${"%.2f".format(cameraSnapshot.zoom)}"

    Column(modifier = modifier) {
        Scene(
            backgroundColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            renderHostFactory = DefaultSceneRenderHostFactory,
            cameraState = cameraState,
            gestures = Gestures.orbitAndZoom(),
        ) {
            PlaygroundScene(
                primitiveCount = primitiveCount,
                showGrid = showGrid,
                showModel = showModel,
            )
        }

        ControlPanel(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            cameraText = cameraText,
            autoRotate = autoRotate,
            onAutoRotateChange = { autoRotate = it },
            showGrid = showGrid,
            onShowGridChange = { showGrid = it },
            showModel = showModel,
            onShowModelChange = { showModel = it },
            primitiveCount = primitiveCount,
            onPrimitiveCountChange = { primitiveCount = it.coerceIn(1, 8) },
            yawSlider = yawSlider,
            pitchSlider = pitchSlider,
            zoomSlider = zoomSlider,
            onResetCamera = {
                scope.launch {
                    cameraState.animateTo(yaw = 25f.deg, pitch = (-15f).deg, zoom = 1.25f)
                    yawSlider = 25f
                    pitchSlider = -15f
                    zoomSlider = 1.25f
                }
            },
            onAnimateCamera = {
                scope.launch {
                    val current = cameraState.snapshot()
                    cameraState.animateTo(
                        yaw = (current.yaw + 90f).deg,
                        pitch = (-18f).deg,
                        zoom = 0.9f,
                    )
                    yawSlider = current.yaw + 90f
                    pitchSlider = -18f
                    zoomSlider = 0.9f
                }
            },
            onYawSliderChange = { value ->
                yawSlider = value
                cameraState.jumpTo(yaw = value.deg, pitch = pitchSlider.deg, zoom = zoomSlider)
            },
            onPitchSliderChange = { value ->
                pitchSlider = value
                cameraState.jumpTo(yaw = yawSlider.deg, pitch = value.deg, zoom = zoomSlider)
            },
            onZoomSliderChange = { value ->
                zoomSlider = value
                cameraState.jumpTo(yaw = yawSlider.deg, pitch = pitchSlider.deg, zoom = value)
            },
        )
    }

    LaunchedEffect(autoRotate) {
        while (autoRotate) {
            withFrameNanos { _ ->
                cameraState.orbitBy(deltaYawDegrees = 0.2f, deltaPitchDegrees = 0f)
            }
        }
    }
}

@Composable
private fun PlaygroundScene(primitiveCount: Int, showGrid: Boolean, showModel: Boolean) {
    val cyclicPositions = remember {
        listOf(
            Triple(0f, 0f, -4f),
            Triple(2f, 0f, -6f),
            Triple(-2f, 0f, -6f),
            Triple(3f, 1f, -8f),
            Triple(-3f, 1f, -8f),
            Triple(0f, 2f, -10f),
            Triple(4f, -1f, -9f),
            Triple(-4f, -1f, -9f),
        )
    }

    // Bundled sample: app/src/main/res/raw/sample_model.glb
    if (showModel) {
        Element.Model(
            model = ModelResource.fromRawResource(R.raw.sample_model),
            modifier = Modifier3D.Default
                .rotateY((-20f).deg)
                .size(1.6f.meters)
                .position(0f.meters, 0.45f.meters, (-3.2f).meters),
        )
    }

    Element.Cube(
        modifier = Modifier3D.Default
            .rotateY(35f.deg)
            .rotateZ(18f.deg)
            .size(1.4f.meters)
            .position(0f.meters, 0f.meters, (-4f).meters),
    )

    if (primitiveCount >= 2) {
        Element.Sphere(
            modifier = Modifier3D.Default
                .size(1f.meters)
                .position(2f.meters, 0f.meters, (-6f).meters),
        )
    }
    if (primitiveCount >= 3) {
        Element.Plane(
            modifier = Modifier3D.Default
                .size(8f.meters, 0.1f.meters, 8f.meters)
                .position(0f.meters, (-1.2f).meters, (-5f).meters),
        )
    }

    for (i in 3 until primitiveCount) {
        val (x, y, z) = cyclicPositions[i % cyclicPositions.size]
        when (i % 3) {
            0 -> Element.Cube(
                modifier = Modifier3D.Default
                    .rotateY((i * 25f).deg)
                    .size(0.8f.meters)
                    .position(x.meters, y.meters, z.meters),
            )
            1 -> Element.Sphere(
                modifier = Modifier3D.Default
                    .size(0.7f.meters)
                    .position(x.meters, y.meters, z.meters),
            )
            else -> Element.Plane(
                modifier = Modifier3D.Default
                    .size(1.5f.meters, 0.08f.meters, 1.5f.meters)
                    .position(x.meters, y.meters, z.meters),
            )
        }
    }

    if (showGrid) {
        for (i in -3..3) {
            Element.Plane(
                modifier = Modifier3D.Default
                    .size(0.04f.meters, 0.02f.meters, 8f.meters)
                    .position((i * 1f).meters, (-1.18f).meters, (-5f).meters),
            )
            Element.Plane(
                modifier = Modifier3D.Default
                    .size(8f.meters, 0.02f.meters, 0.04f.meters)
                    .position(0f.meters, (-1.18f).meters, (i * 1f - 5f).meters),
            )
        }
    }
}

@Composable
private fun ControlPanel(
    modifier: Modifier = Modifier,
    cameraText: String,
    autoRotate: Boolean,
    onAutoRotateChange: (Boolean) -> Unit,
    showGrid: Boolean,
    onShowGridChange: (Boolean) -> Unit,
    showModel: Boolean,
    onShowModelChange: (Boolean) -> Unit,
    primitiveCount: Int,
    onPrimitiveCountChange: (Int) -> Unit,
    yawSlider: Float,
    onYawSliderChange: (Float) -> Unit,
    pitchSlider: Float,
    onPitchSliderChange: (Float) -> Unit,
    zoomSlider: Float,
    onZoomSliderChange: (Float) -> Unit,
    onResetCamera: () -> Unit,
    onAnimateCamera: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            if (showModel) {
                "Spatial Playground — sample_model.glb"
            } else {
                "Spatial Playground — primitives only"
            },
            style = MaterialTheme.typography.titleSmall,
            fontFamily = FontFamily.Monospace,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .padding(8.dp),
        ) {
            Text(
                cameraText,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF84CC16),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = onAnimateCamera, modifier = Modifier.weight(1f)) {
                Text("Animar")
            }
            Button(onClick = onResetCamera, modifier = Modifier.weight(1f)) {
                Text("Reset")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabeledSwitch("Auto-rotate", autoRotate, onAutoRotateChange, Modifier.weight(1f))
            LabeledSwitch("Grid", showGrid, onShowGridChange, Modifier.weight(1f))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabeledSwitch("Modelo GLB", showModel, onShowModelChange, Modifier.weight(1f))
            Text(
                if (showModel) "R.raw.sample_model" else "Solo primitivas",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
            )
        }

        LabeledSlider(
            label = "Primitivas: $primitiveCount",
            value = primitiveCount.toFloat(),
            valueRange = 1f..8f,
            steps = 6,
            onValueChange = { onPrimitiveCountChange(it.toInt()) },
        )
        LabeledSlider(
            label = "Yaw: ${"%.1f".format(yawSlider)}°",
            value = yawSlider,
            valueRange = -180f..180f,
            onValueChange = onYawSliderChange,
        )
        LabeledSlider(
            label = "Pitch: ${"%.1f".format(pitchSlider)}°",
            value = pitchSlider,
            valueRange = -85f..85f,
            onValueChange = onPitchSliderChange,
        )
        LabeledSlider(
            label = "Zoom: ${"%.2f".format(zoomSlider)}x",
            value = zoomSlider,
            valueRange = 0.5f..3f,
            onValueChange = onZoomSliderChange,
        )

        Text(
            "Asset: res/raw/sample_model.glb via ModelResource.fromRawResource.\n" +
                "Gestos: 1-dedo orbit · 2-dedos pinch zoom",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun LabeledSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    steps: Int = 0,
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
        Slider(
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange,
            steps = steps,
        )
    }
}
